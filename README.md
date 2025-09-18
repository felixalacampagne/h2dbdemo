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

Not sure about importing the CSV into H2.

