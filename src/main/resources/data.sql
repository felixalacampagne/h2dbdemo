-- noinspection SqlNoDataSourceInspectionForFile

insert into account (acc_id,acc_addr,acc_code,acc_curr,acc_desc,acc_fmt,acc_order,acc_sid,acc_swiftbic,acc_tel) values
                    (1     ,'addr'  ,'code'  ,'EUR'   ,'desc'  ,'#.00' ,'99'     ,'sid'  ,'bic'       ,'tel'  ),
                    (2     ,'addr2'  ,'code2'  ,'EUR'   ,'desc'  ,'#.00' ,'99'     ,'sid'  ,'bic'       ,'tel'  ),
                    (99    ,'addr99'  ,'code99'  ,'EUR'   ,'desc'  ,'#.00' ,'99'     ,'sid'  ,'bic'       ,'tel'  );

ALTER SEQUENCE account_seq RESTART WITH (select max(acc_id)+1 from account);

