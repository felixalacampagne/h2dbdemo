package com.felixalacampagne.account.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.felixalacampagne.account.persistence.entities.PhoneAccount;
import com.felixalacampagne.account.persistence.entities.PhoneWithAccountProjection;

@Repository
public interface PhoneAccountJpaRepository extends JpaRepository<PhoneAccount, Long>
{

   // NB. if accountId is null then 'p.accountId != :exaccid' is false so nulls are also excluded.
   // the accountId is 0 for entries without an entry in Accounts
   @Query("SELECT p FROM PhoneAccount p where p.account.id != :exaccid and p.order not in (255) "
        + "order by p.order DESC, p.desc ASC" )
   List<PhoneAccount> findTransferAccounts(@Param("exaccid") Long excludeAccountId);

   // PhoneWithAccountProjection is pretty much redundant when PhoneAccount uses ManyToOne with an Account object
   // This is used so that the Account desc is used instead of the values in PhoneAccount.
   @Query("SELECT p as phoneAccount, a.accDesc as accDesc, a.accCode as accCode FROM PhoneAccount p LEFT JOIN Account a "
         + "ON  p.account.id = a.id where p.account.id != :exaccid and p.order not in (255) "
         + "order by p.order DESC, p.desc ASC" )
   List<PhoneWithAccountProjection> findTransferAccountsWithAccount(@Param("exaccid") Long excludeAccountId);

   @Query("SELECT p as phoneAccount, a.accDesc as accDesc, a.accCode as accCode FROM PhoneAccount p LEFT JOIN Account a "
         + "ON  p.account.id = a.id "
         + "order by p.order DESC, p.desc ASC" )
   List<PhoneWithAccountProjection> findAllWithRelatedDetails();


   @Query("SELECT p as phoneAccount, a.accDesc as accDesc, a.accCode as accCode "
         + "FROM PhoneAccount p LEFT JOIN Account a ON  p.account.id = a.id "
         + "where p.Id = :Id")
   Optional<PhoneWithAccountProjection> findPhoneWithAccountById(@Param("Id") Long id);

   long deleteByAccountId(Long accId);

}
