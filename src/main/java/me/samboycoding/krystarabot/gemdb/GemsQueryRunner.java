/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.samboycoding.krystarabot.gemdb;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Emily Ash
 */
public class GemsQueryRunner
{
    private static QueryRunner queryRunner;
    private static Object lock = new Object();
    
    public static QueryRunner getQueryRunner() throws SQLException, IOException
    {
        synchronized (lock)
        {
            if (queryRunner == null)
            {
                String userData = null;
                
                // Look for a password file
                try
                {
                    userData = FileUtils.readFileToString(new File("../gems.user"), Charset.defaultCharset());
                }
                catch (IOException e)
                {
                    userData = FileUtils.readFileToString(new File("./gems.user"), Charset.defaultCharset());
                }
                
                String[] creds = userData.split(":");

                BasicDataSource basicDataSource = new BasicDataSource();
                basicDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
                basicDataSource.setUsername(creds[0]);
                basicDataSource.setPassword(creds[1]);
                basicDataSource.setUrl("jdbc:mysql://mysql.ashtender.com:3306/gems");
                basicDataSource.setValidationQuery("SELECT 1");
                queryRunner = new QueryRunner(basicDataSource);
            }
            return queryRunner;
            
        }
    }
}
