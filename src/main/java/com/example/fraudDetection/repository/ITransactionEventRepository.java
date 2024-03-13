package com.example.fraudDetection.repository;

import com.example.fraudDetection.entity.TransactionEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;


import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface ITransactionEventRepository extends JpaRepository<TransactionEvent, Long> {
    List<TransactionEvent> findAllByTimestampAfterAndUserID(Long timeStamp,String UserId);
    List<TransactionEvent> findAllByTimestampAfter(Long timeStamp);

}



