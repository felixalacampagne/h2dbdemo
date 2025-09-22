# h2dbdemo

Project to explore migrating the content of an MS-Access DB to an H2 DB. 

Not sure whether this is the DB I'll use but the alternatives appears to be OTT for my needs
where OTT means 'way to much hassle and probably not available where it is needed'.

The opion of Google appears to be that PostgreSQL might be a better thing to use... 
and then there is the infamous MySql, of LAMP fame.

The DB can be created from scratch containing the tables defined by the Entities. It can then be populated
with a series of insert statements taken from the 'data.sql' file. As a one of thing this could be done
via the IDE if 'data.sql' can be created for the content of the Access. One problem with this is that the
IDs of the imported data must be preserved in order to maintain the foreign key relationships - after the import
the automatic sequence need to be updated to point beyond the ID of the imported data. I couldn't find a
way to do this for the 'IDENTITY' @GeneratedValue strategy but using a 'SEQUENCE' strategy and resetting the sequence
in the 'data.sql' seems to work OK, eg.

ALTER SEQUENCE account_seq RESTART WITH (select max(acc_id)+1 from account);

Note also the way of inserting multiple without needing to specify the column names for each insert - no idea
if this is a special H2 format but it might make exporting the Access data easier although the Google choice for doing
this is using CSV.

UCanAccess may have a way to export data as CSV. It requires use of the ucanaccess command line tool: https://ucanaccess.sourceforge.net/site.html#clients which in turn seems to rely on a script called console.bat in order to run. Trying to use the maven jar file didn't work.
console.bat in the download zip is different to the one in github. It contains the following

```
@echo off
set PATH=%PATH%;.
set BASE_DIR=%~f0

:CONT
SET RMVD=%BASE_DIR:~-1%
SET BASE_DIR=%BASE_DIR:~0,-1%
if NOT "%RMVD%"=="\" goto CONT

SET UCANACCESS_HOME=%BASE_DIR%
SET LOCAL_HOME_JAVA="%JAVA_HOME%"
if exist %LOCAL_HOME_JAVA%\bin\java.exe (
  SET LOCAL_JAVA=%LOCAL_HOME_JAVA%\bin\java.exe
) else (
  SET LOCAL_JAVA=java.exe
)

%LOCAL_JAVA% -version
@echo.

SET CLASSPATH="%UCANACCESS_HOME%\lib\hsqldb-2.7.3.jar;%UCANACCESS_HOME%\lib\jackcess-4.0.5.jar;%UCANACCESS_HOME%\lib\commons-lang3-3.14.0.jar;%UCANACCESS_HOME%\lib\commons-logging-1.2.jar;%UCANACCESS_HOME%\ucanaccess-5.1.1.jar"

%LOCAL_JAVA% -classpath %CLASSPATH% net.ucanaccess.console.Main
pause

```

For ucanaccess-5.1.1 the lib sub-directory must contain:
 - commons-logging-1.2.jar
 - (org/hsqldb) hsqldb-2.7.3.jar
 - (com/healthmarketscience) jackcess-4.0.5.jar
 - (org/apache/commons) commons-lang3-3.14.0.jar

Obviously migrating the data from Access to H2 using CSV is not as straight forward as it should be, mainly because
the documentation leaves out certain fundamental bits of information, presumably so some consultant somewhere can
demand a huge fee to get a trivial thing working.

The export from UCanaccess is fairly straight forward once the package is downloaded - there is a console.bat, as indicated 
by the documentation, and running it does connect to the database. Exporting the tables is also simple enough;

    export -t account ./account.csv;
    export -t transaction ./transaction.csv;
    export -t standingorders ./standingorders.csv;
    export -t PhoneAccounts ./phoneaccount.csv;
    export -t PhoneTrans ./phonetransaction.csv;
    export -t Prefs ./prefs.csv;

The column headers need to be modified to match the new column names.

The H2 import requires a comma (the delimiter can be specified as I discovered later when I finally found some sort
of reference to the import function).

The documentation for the H2 import says use 'INSERT INTO ... SELECT' and then gives some example selects - absolutely 
nothing for how to use with the INSERT and doing what it says gives an error indicating that the columns in the CSV are
being completely ignored (can't put a 'code' into 'ranking' field). 
So then follows hours of fiddling around with the punctuation, case, whatever to try to get it to use the columns 
in the CSV file like it says it should be doing. 
Eventually I stumbled across an SO post which just happened to mention that the columns MUST be included in the 
insert statement, eg. 'INSERT INTO TABLE {COL1,COL2...) SELECT' where the order of the columns MUST duplicate
the order of the columns in the CSV file (the post is actually asking why on earth this is necessary!!). 
When this is done the import happens without an issue - Why The Fork don't they say that in the documentation 
since it is so blindly not obvious that the information must be duplicated.

So the eventual command to the account was;

    insert into account (id,code,description,address,contact,currency,currencyformat,statementref,ranking,swiftbic) 
    SELECT * FROM CSVREAD('src/test/resources/csv/account_h2cols.csv', null, 'charset=UTF-8 fieldSeparator=;');

Needless to say transaction did not work without issue. The dates in the exported CSV are not compatible with the date column
type created via the entity - the time part must be removed. For some weird reason the time part is not always 00:00:00
which means a regex must be used for the removal. Once the date is fixed the import seems to be OK.

It may be possible to avoid the date fix by specifying the conversion in the select statement, this would require an
additional duplication of the column names with something like 

"cast(parsedatetime('transactiondate', 'yyyy-MM-dd HH:mm:ss') as date) as 'transactiondate'" 

for the date column - something to try... need to check the format etc.

WARNING: using 'date' as the column name is going to cause all sorts of grief because 'DATE' is an 
SQL keyword (like 'ORDER' is). So better change it to something transactiondate - likewise for 'type'.

    insert into transaction (id,accountid,transactiondate,transactiontype,comment,checked,credit,debit,balance,checkedbalance,sortedbalance,statementref) 
    SELECT id,accountid, cast(parsedatetime(transactiondate, 'yyyy-MM-dd HH:mm:ss') as date) as transactiondate, type,comment,checked,credit,debit,balance,checkedbalance,sortedbalance,statementref 
    FROM CSVREAD('src/test/resources/csv/transaction_h2cols.csv', null,  'charset=UTF-8 fieldSeparator=;');

Standingorder contains dates:

    insert into standingorder (id  ,period  ,count  ,paydate      ,entrydate  ,comment,accountid,transactiontype,amount) 
    SELECT id  ,period  ,count  ,
    cast(parsedatetime(paydate, 'yyyy-MM-dd HH:mm:ss') as date) as paydate,
    cast(parsedatetime(entrydate, 'yyyy-MM-dd HH:mm:ss') as date) as entrydate,
    comment,accountid,transactiontype,amount
    FROM CSVREAD('src/test/resources/csv/standingorders_h2cols.csv', null,  'charset=UTF-8 fieldSeparator=;');

phoneaccount contains obsolete columns:

    insert into phoneaccount (id  ,accounttype,accountid,code    ,comment,ranking,communication) 
    SELECT id  ,accounttype,accountid,code    ,comment,ranking,communication FROM CSVREAD('src/test/resources/csv/phoneaccount_h2cols.csv', null, 'charset=UTF-8 fieldSeparator=;');

phonetransaction contains null dates as '(null)' which causes an issue with the date parsing:

    insert into phonetransaction (id  ,paydate  ,senderphoneaccountid,recipientphoneaccountid,amount  ,communication,comment,sentdate  ,transactiondate,errorstatus) 
    SELECT id  ,
    cast(parsedatetime(paydate, 'yyyy-MM-dd HH:mm:ss') as date) as paydate,
    senderphoneaccountid,recipientphoneaccountid,amount  ,communication,comment,
    cast(parsedatetime(sentdate, 'yyyy-MM-dd HH:mm:ss') as date) as sentdate,
    cast(parsedatetime(transactiondate, 'yyyy-MM-dd HH:mm:ss') as date) as transactiondate,
    errorstatus 
    FROM CSVREAD('src/test/resources/csv/phonetransaction_h2cols.csv', null, 'charset=UTF-8 fieldSeparator=; null=(null)');

prefs in Access does not have an id column but H2 will have an ID which needs to be supplied automatically
when the CSV is imported. It appears that this does NOT happen when the generation strategy is SEQUENCE but 
can be provided via the select statement by querying the next sequence value (NB. NEXT VALUE FOR does not work):

    insert into prefs (id, name,text,numeric) 
    SELECT (select nextval('prefs_seq') ) as id, name,text,numeric FROM CSVREAD('src/test/resources/csv/prefs_h2cols.csv', null, 'charset=UTF-8 fieldSeparator=; null=(null)');
