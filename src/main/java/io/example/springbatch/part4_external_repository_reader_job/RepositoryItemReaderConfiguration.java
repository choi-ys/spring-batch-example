package io.example.springbatch.part4_external_repository_reader_job;

import io.example.springbatch.part4_external_repository_reader_job.repository.PersonEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @author : arura
 * @date : 2021-08-01 오후 5:24
 * @apiNote :
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class RepositoryItemReaderConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final PersonEntityRepository personEntityRepository;

    @Bean
    public Job repositoryItemReaderJob() throws Exception {
        return jobBuilderFactory.get("repositoryItemReaderJob")
                .incrementer(new RunIdIncrementer())
                .start(this.repositoryItemReaderStep())
                .build();
    }

    @Bean
    public Step repositoryItemReaderStep() throws Exception {
        return stepBuilderFactory.get("repositoryItemReaderStep")
                .<PersonEntity, PersonEntity>chunk(2)
                .reader(repositoryItemReader())
                .writer(itemWriter())
                .build();
    }

    public RepositoryItemReader repositoryItemReader() throws Exception {
        RepositoryItemReader<PersonEntity> personEntityRepositoryItemReader = new RepositoryItemReaderBuilder()
                .name("repositoryItemReader")
                .repository(personEntityRepository)
                .methodName("findByAgeBefore")
                .pageSize(2)
//                .maxItemCount(2)
                .arguments(31)
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
        personEntityRepositoryItemReader.afterPropertiesSet();
        return personEntityRepositoryItemReader;
    }

    private ItemWriter<PersonEntity> itemWriter() {
        return items -> log.info("Result : {}", items.stream()
                .map(PersonEntity::getName)
                .collect(Collectors.joining(", "))
        );
    }
}