/*
 * Copyright 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.mongo.example;

import com.mongodb.DB;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.mongo.config.Database;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ContextConfiguration(locations = {"classpath:application-config.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ExampleJobConfigurationTests {

    @Inject
    private JobOperator jobOperator;

    @Inject
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Inject
    @Database(Database.Purpose.BATCH)
    private DB batchDB;

    @Before
    public void setUp() {
        batchDB.dropDatabase();
    }

    /**
     * Create a unique job instance and check it's execution completes
     * successfully - uses the convenience methods provided by the testing
     * superclass.
     *
     * @throws Exception when shit happens
     */
    @Test
    public void testLaunchJob() throws Exception {

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobLauncherTestUtils.getUniqueJobParameters());
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }

    /**
     * Execute a fresh {@link JobInstance} using {@link JobOperator} - closer to
     * a remote invocation scenario.
     *
     * @throws Exception when shit happens
     */
    @Test
    public void testLaunchByJobOperator() throws Exception {

        // assumes the job has a JobIncrementer set
        long jobExecutionId = jobOperator.startNextInstance(jobLauncherTestUtils.getJob().getName());

        // no need to wait for job completion in this case, the job is launched
        // synchronously

        String result = jobOperator.getSummary(jobExecutionId);
        assertTrue(result.contains("status=" + BatchStatus.COMPLETED));

    }

}
