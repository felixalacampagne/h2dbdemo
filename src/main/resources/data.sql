-- noinspection SqlNoDataSourceInspectionForFile

--          account (acc_id,acc_addr,acc_code,acc_curr,acc_desc   ,acc_fmt       ,acc_order,acc_sid     ,acc_swiftbic,acc_tel) values
insert into account (id    ,address , code   ,currency,description,currencyformat,ranking  ,statementref,swiftbic    ,contact) values
                    (1     ,'addr'  ,'code'  ,'EUR'   ,'desc'     ,'#.00'        ,'99'     ,'sid'       ,'bic'       ,'tel'  ),
                    (2     ,'addr2' ,'code2' ,'EUR'   ,'desc'     ,'#.00'        ,'99'     ,'sid'       ,'bic'       ,'url'  ),
                    (99    ,'addr99','code99','EUR'   ,'desc'     ,'#.00'        ,'99'     ,'sid'       ,'bic'       ,'email');

ALTER SEQUENCE account_seq RESTART WITH (select max(id)+1 from account);

