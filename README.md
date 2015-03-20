CSE 454 '15 WI 
Mentors: Dan Weld, Stephen Soderland, Xiao Ling

					     Common Sense Database Build 
					  By Amrita Mohan Bryan Djunaedi York Wei 


An external UI running the project can be located at: http://ec2-54-69-54-128.us-west-2.compute.amazonaws.com/


Instructions for Deploying Common Sense Database from scratch:  

 REQUIREMENTS
---------------
Web Server access (we use Apache webserver httpd)
MongoDB installed on your server

 DOWNLOAD
-----------

1. Download commonsense.zip and unzip; preferrably in a server where you may also run a virtual web server.

2. Web Table data can be found at http://data.dws.informatik.uni-mannheim.de/webtables/2014-02/completeCorpus/
   - Please download the webtable bundle(s) of your choosing. Keep in mind the memory constraints and these are extremely 
     large files. 

3. Extract bundles with tar -xvf command

 FILTERING
------------
There is a lot of noise in the web tables that you download, so you may use one or more of the filters we created
to filter out as much noise as possible.[Optional Language Filter available]


4. Access filters in the commonsense directory that you download in the src/ directory

5. The first filter deployed in the Common Sense DB is the transpose filter located in the commonsense/src/Transpose directory
   - Run transpose.java on the directory where you stored your web tables using the java command and passing in the web tables 
     directory as the argument. This may take over an hour to run given the size of the data and runtime of transposing a matrix. 

6. When completed, use the attrbute filter located in commonsense/src/commonsense/AttributeFilter.java. 
   - Create an instance of AttibuteFilter passing in the attribute.json file located under the main commonsense/ directory and 
     the units.json file also at that location. 
   - for all files in your transposed web table directory you can now classify their relevance to object size. 

7. Unit Conversions

8. Freebase Caller 

9. Table Crawler



 WEB/UI DEPLOYMENT
--------------------
10. There should now be a crawl *.txt file outputted from the Table Crawler results to the file name of your choice.  
    Under main commonsense/ dir you should see an html.zip file. This will be the code you will use to deploy your website. 
    - Unzip this file in the web directory for your website and perform the mv /* . command to move its content into that directory. 
    - Move your crawl file into your web directory with mv command

11. You will see a jar file in your web directory called dbbuild.jar. Assuming you have MongoDB installed correctly on your server
    you can simply pass your crawl file as an argument into dbbuild and it will create your Database under the name "commonsense" and the 
    collection "relations".
    - Do so using the command java -jar dbbuild.jar [-CRAWLFILE.TXT]

12. Since the web code has already been written for you and should be located in your web directory, and your database has been built, 
    ensure that your local web server is running and then your own version of The Common Sense DB should be running at your 
    public DNS address 
    - Modify webpage with corresponding html pages located in your web directory. Should be there when you unpacked html in step 10. 



  
