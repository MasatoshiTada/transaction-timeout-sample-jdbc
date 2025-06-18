package com.example.transactiontimeoutsamplejdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SampleService {
  private static final Logger logger = LoggerFactory.getLogger(SampleService.class);

  private final SampleRepository sampleRepository;

  public SampleService(SampleRepository sampleRepository) {
    this.sampleRepository = sampleRepository;
  }

  @Transactional(timeout = 2, readOnly = false)
  public void registerWithSleep(Sample sample, int seconds) {
    logger.info("Sleep {}seconds...", seconds);
    sampleRepository.sleep(seconds);
    logger.info("Sleep completed. Starting INSERT...");
    sampleRepository.insert(sample);
    logger.info("INSERT completed.");
  }
}
