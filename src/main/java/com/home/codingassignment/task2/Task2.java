package com.home.codingassignment.task2;

import com.home.codingassignment.task2.entity.Bets;
import com.home.codingassignment.task2.model.Market;
import com.home.codingassignment.task2.model.Match;
import com.home.codingassignment.task2.model.Outcome;
import com.home.codingassignment.task2.repository.BetsRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.fasterxml.jackson.databind.cfg.CoercionInputShape.Array;

public class Task2 {

    private static BetsRepository betsRepository;

    public Task2(BetsRepository betsRepository) {
        Task2.betsRepository = betsRepository;
    }

    public static void main(String[] args) throws SQLException {

    }

    public static void readAndSaveBets(String filePath) throws FileNotFoundException, SQLException {

        // save all match_ids into a new array
        HashMap<String, Match> listOfMatches = new HashMap<>();

        try {

            String matchId;
            Long marketId;
            String outcomeId;
            String specifiers;

            File myObj = new File(filePath);
            Scanner myReader = new Scanner(myObj);

            boolean readHeader = false;

            // read the file until its empty
            while (myReader.hasNextLine()) {

                String line = myReader.nextLine();

                // skip the header
                if (!readHeader) {
                    readHeader = true;
                    if (line.toUpperCase().contains("MATCH_ID|MARKET_ID|OUTCOME_ID|SPECIFIERS")) {
                        continue;
                    }
                }

                // split the data with the pipe delimiter, we can get at most 4 elements
                List<String> data = Arrays.asList(line.split("\\|", 4));

                if (data.size() >= 3) {

                    matchId = data.get(0);
                    marketId = Long.valueOf(data.get(1));
                    outcomeId = data.get(2);
                    specifiers = null;

                    if (data.size() == 4) {
                        specifiers = data.get(3).trim();
                    }

                    if (specifiers.isEmpty()) {
                        specifiers = null;
                    }

                    // if a match with this key doesn't we create another match
                    // if it does exist we add a new market to the existing match
                    Match match = listOfMatches.get(matchId);

                    if (match == null) {
                        listOfMatches.put(matchId, new Match(matchId, marketId, outcomeId, specifiers));
                    } else {
                        match.addMarket(marketId, outcomeId, specifiers);
                    }

                }

            }

            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        // after processing the file, we can save all the matches
        saveMatches(listOfMatches);

    }

    public static void saveMatches(HashMap<String, Match> listOfMatches) throws SQLException {

        long insert_id = 1L; // this variable is used to check the final order in the db
        ArrayList<Bets> bets = new ArrayList<>();

        long startTime = System.nanoTime();


        // traverse through all matches
        for (Map.Entry<String, Match> match : listOfMatches.entrySet()) {
            // sort the data in a single match and traverse through all the markets
            for (Map.Entry<Long, Market> market : match.getValue().sort().getMarkets().entrySet()) {
                // traverse through all the outcomes
                for (Map.Entry<String, Outcome> outcome : market.getValue().getOutcomes().entrySet()) {
                    // traverse through all the specifiers
                    for (String specifiers : outcome.getValue().getSpecifiers()) {

                        bets.add(new Bets(insert_id, match.getKey(), market.getKey(), outcome.getKey(), specifiers));
                        insert_id += 1;

                    }
                }
            }
        }


        long endTime = System.nanoTime();

        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.

        System.out.println("Sorting time in milliseconds:      " + duration / 1000000);

        startTime = System.nanoTime();


        // betsRepository.saveAll(bets);

        saveWithCopy(bets);

        // saveInBatches(bets, 1000000);

        endTime = System.nanoTime();

        duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.

        System.out.println("DB insertion time in milliseconds: " + duration / 1000000);

    }

    /**
     * Saves the data with prepared statements
     */
    public static void saveInBatches(List<Bets> bets, int batchSize) throws SQLException {
        int totalBatches = (bets.size() + batchSize - 1) / batchSize;
        int counter = 0;

        try(Connection connection = getDataSource().getConnection()) {
            String insertBetsSql = "INSERT INTO BETS(insert_id, match_id, market_id, outcome_id, specifiers) "
                    + "VALUES (?,?,?,?,?)";

            for (int batchNum = 0; batchNum < totalBatches; batchNum++) {

                PreparedStatement statement = connection.prepareStatement(insertBetsSql);

                for (int i = 0; i < batchSize && counter < bets.size(); i++, counter++) {

                    Bets currentBet = bets.get(counter);

                    statement.setInt(1, counter);
                    statement.setString(2, currentBet.getMatchId());
                    statement.setLong(3, currentBet.getMarketId());
                    statement.setString(4, currentBet.getOutcomeId());
                    statement.setString(5, currentBet.getSpecifiers());

                    statement.addBatch();

                }

                statement.executeBatch();
            }
        }

    }

    /**
     * Saves the data with the COPY command
     */
    public static void saveWithCopy(List<Bets> bets) throws SQLException {

        try (Connection connection = getDataSource().getConnection()) {

            CopyManager copyManager = new CopyManager(connection.unwrap(BaseConnection.class));

            StringBuilder sb = new StringBuilder();

            for (int counter = 0; counter < bets.size(); counter++) {

                    Bets currentBet = bets.get(counter);

                    // Format each line as tab-separated values
                    sb.append(counter+1).append('\t') // insert_id
                        .append(nullToNullString(currentBet.getMatchId())).append('\t')
                        .append(currentBet.getMarketId()).append('\t')
                        .append(nullToNullString(currentBet.getOutcomeId())).append('\t')
                        .append(nullToNullString(currentBet.getSpecifiers())).append('\n');
                }

                StringReader reader = new StringReader(sb.toString());

                copyManager.copyIn(
                        "COPY BETS(insert_id, match_id, market_id, outcome_id, specifiers) FROM STDIN WITH (FORMAT text)",
                        reader
                );

        } catch (Exception e) {
            throw new SQLException("Error during batch insert", e);
        }
    }

    private static String nullToNullString(String value) {
        return value == null ? "\\N" : value;
    }


    private static PGSimpleDataSource getDataSource(){

        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerName("localhost");
        dataSource.setDatabaseName("mydatabase");
        dataSource.setUser("myuser");
        dataSource.setPassword( "mypassword" );

        return dataSource;

    }
}



