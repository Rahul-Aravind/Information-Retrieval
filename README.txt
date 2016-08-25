Download cygwin64 terminal.
Download solr 4.10

Download a binary package (apache-nutch-1.9-bin.zip) from here. (http://www.apache.org/dyn/closer.cgi/nutch/)
Unzip your binary Nutch package. There should be a folder apache-nutch-1.9.
cd apache-nutch-1.X/

cd E:/poornima/2016-spring/IR/search-engine/downloads/apache-nutch-1.9

run "bin/nutch" - You can confirm a correct installation if you see something similar to the following:

Usage: nutch COMMAND where command is one of:
readdb            read / dump crawl db
mergedb           merge crawldb-s, with optional filtering
readlinkdb        read / dump link db
inject            inject new urls into the database
generate          generate new segments to fetch from crawl db
freegen           generate new segments to fetch from text files
fetch             fetch a segment's pages
...

chmod +x bin/nutch  - if u get permission denied

export JAVA_HOME="C:/Program Files (x86)/Java/jre1.8.0_77"

echo $JAVA_HOME


 Add your agent name in the value field of the http.agent.name property in conf/nutch-site.xml / conf/nutch-default.xml , for example:

<property>
 <name>http.agent.name</name>
 <value>My Nutch Spider</value>
</property>

mkdir -p urls
cd urls
touch seed.txt -- create the file that contains seeds


  do config file setup and download the patch as mentioned : (https://github.com/congainc/patch-hadoop_7682-1.0.x-win/downloads)
  I was getting the same exception while runing nutch-1.7 on windows 7.

bin/nutch crawl urls -dir crawl11 -depth 1 -topN 5
The following steps worked for me

Download the pre-built JAR, patch-hadoop_7682-1.0.x-win.jar, from theDownload section. You may get the steps for hadoop.
Copy patch-hadoop_7682-1.0.x-win.jar to the ${NUTCH_HOME}/lib directory
Modify ${NUTCH_HOME}/conf/nutch-site.xml to enable the overriden implementation as shown below:

<?xml version="1.0"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<!-- Put site-specific property overrides in this file. -->
<configuration>
    <property>
        <name>fs.file.impl</name>
        <value>com.conga.services.hadoop.patch.HADOOP_7682.WinLocalFileSystem</value>
        <description>Enables patch for issue HADOOP-7682 on Windows</description>
    </property>
 </configuration>
Run your job as usual (using Cygwin).


bin/nutch inject crawl/crawldb urls

bin/nutch generate crawl/crawldb crawl/segments
s1=`ls -d crawl/segments/2* | tail -1`
echo $s1
bin/nutch fetch $s1
bin/nutch parse $s1
bin/nutch updatedb crawl/crawldb $s1

bin/nutch generate crawl/crawldb crawl/segments
s2=`ls -d crawl/segments/2* | tail -1`
echo $s2
bin/nutch fetch $s2
bin/nutch parse $s2
bin/nutch updatedb crawl/crawldb $s2

bin/nutch generate crawl/crawldb crawl/segments -topN 1000
s3=`ls -d crawl/segments/2* | tail -1`
echo $s3
bin/nutch fetch $s3
bin/nutch parse $s3
bin/nutch updatedb crawl/crawldb $s3

bin/nutch mergesegs crawl/merged -dir crawl/segments  -- merge segments

bin/nutch webgraph -segment crawl/merged -webgraphdb crawl/webgraphdb -filter -normalize  -- getweb data

bin/nutch readseg -dump crawl/merged/* crawl/dumps  -- dump content

bin/nutch readlinkdb crawl/linkdb -dump crawl/graph --- dump inlink-outlinks

bin/nutch invertlinks crawl/linkdb -dir crawl/segments

Usage: bin/nutch solrdedup <solr url>
     Example: /bin/nutch solrdedup http://localhost:8983/solr   -- deleting duplicates

Usage: bin/nutch solrclean <crawldb> <solrurl>
     Example: /bin/nutch solrclean crawl/crawldb/ http://localhost:8983/solr   -- cleaning solr

	  bin/nutch readdb crawl/crawldb -stats   --- get statistics

 bin/crawl urls/ crawl http://localhost:8983/solr 2
 
bin/nutch org.apache.nutch.scoring.webgraph.Loops -webgraphdb crawl/webgraphdb/
bin/nutch org.apache.nutch.scoring.webgraph.LinkRank -webgraphdb crawl/webgraphdb/
bin/nutch org.apache.nutch.scoring.webgraph.ScoreUpdater -crawldb crawl/crawldb -webgraphdb crawl/webgraphdb/
bin/nutch org.apache.nutch.scoring.webgraph.NodeDumper -scores -topn 1000 -webgraphdb crawl/webgraphdb/ -output crawl/webgraphdb/dump/scores  

SOLR apache Execution steps

download binary file from here (http://www.apache.org/dyn/closer.cgi/lucene/solr/)
unzip to $HOME/apache-solr, we will now refer to this as ${APACHE_SOLR_HOME}
cd ${APACHE_SOLR_HOME}/example
java -jar start.jar  -- this doent worked

Integrate solr with nutch:
mv ${APACHE_SOLR_HOME}/example/solr/collection1/conf/schema.xml ${APACHE_SOLR_HOME}/example/solr/collection1/conf/schema.xml.org
cp ${NUTCH_RUNTIME_HOME}/conf/schema.xml ${APACHE_SOLR_HOME}/example/solr/collection1/conf/

Open the Nutch schema.xml file for editing:
vi ${APACHE_SOLR_HOME}/example/solr/collection1/conf/schema.xml

Comment out the following lines (53-54) in the file by changing this:
   <filter class="solr.
EnglishPorterFilterFactory" protected="protwords.txt"/>
to this
<!--   <filter class="solr.
EnglishPorterFilterFactory" protected="protwords.txt"/> -->
Add the following line right after the line <field name="id" ... /> (probably at line 69-70)
<field name="_version_" type="long" indexed="true" stored="true"/>
If you want to see the raw HTML indexed by Solr, change the content field definition (line 80) to:
<field name="content" type="text" stored="true" indexed="true"/>

bin/solr.cmd start

//bin/nutch solrindex http://127.0.0.1:8983/solr/ crawl/crawldb -linkdb crawl/linkdb crawl/segments/
 bin/nutch solrindex http://localhost:8983/solr crawl/crawldb/ -linkdb crawl/linkdb/ crawl/segments/20131108063838/ -filter -normalize
 
 http://localhost:8983/solr/admin/

 steps :
 1. invertlinks
 2. dedup
 3. indexing
 4. cleaning
 
 Import the search engine project. load the project to apache server 7.0.
 
 Load the web page with the url "http://localhost:8080/searchengine/faces/search.xhtml".