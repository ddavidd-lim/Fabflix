# **CS 122B Project 5**

- # General
    - #### Team#: Proj:Zot
    
    - #### Names: Caitlynn Chang & David Lim
    
    - #### Project 5 Video Demo Link:

    - #### Instruction of deployment:

    - #### Collaborations and Work Distribution:


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
    
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
    
    - #### Explain how Connection Pooling works with two backend SQL.
    

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.

    - #### How read/write requests were routed to Master/Slave SQL?
    

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.


- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTPS/10 threads                       | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 4: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |

# **CS 122B Project 4**

## Demo Video
https://www.youtube.com/watch?v=T3aWGcP8wos

## Contributions

Caitlynn Chang
- Implemented Autocomplete

David Lim
- Implemented Andoid

# **CS 122B Project 3**

## Demo Video
https://youtu.be/VF5bSHseUDs

## Contributions

Caitlynn Chang
- Implemented Dashboard using Stored Procedure
- Implemented PreparedStatements
- Implemented recatpcha
- Implemented HTTPS
- Implemented Encrypted Password

David Lim
- Importing large XML data files into database

## Files with Prepared Statements
- AddMovieServlet.java
- AddStarServlet.java
- CreditCardServlet.java
- EmployeeLoginServlet.java
- ItemsServlet.java
- LoginServlet.java
- SearchServlet.java
- SingleMovieServlet.java
- SingleStarServelet.java

## Parsing time optimization strategies
1. Less to SQL
2. Used a Hashmap while parsing through data to allow constant time getting

## Inconsistent data reports from parsing
In ReferenceInconsistencyReport.txt. Can be seen in video, but named InconsistencyReport.txt.

# **CS 122B Project 2**

## Demo Video
https://www.youtube.com/watch?v=3YMf_IH_Jrw

## Contributions

Caitlynn Chang
- Main Page (search and browse)
- Single Star/Movie Pages + Jump Functionaility
- Movie List Page 


David Lim
- Login Page
- Shopping Cart + Payment Pages and Functions
- Demo Video

## Where Used LIKE/ILIKE
LIKE was mainly used for the Search and Browse Function (Servlets.SearchServlet). For the search feature, LIKE was used for finding the title, director and star with the pattern being %substring%. For the browsing feature, LIKE was used for genres with the format %genre% and movie titles with the format pattern% (except for *). Genre was formatted this way due to query returning genres as a string of all the genres. 
 
# **CS 122B Project 1**

## Demo Video
https://youtu.be/39aJCIOgjM8?si=dw449bWWeoUZefQS


## Contributions

Caitlynn Chang
- Single movie and single star pages
- SQL Create Table
- Edited HTML\CSS files


David Lim
- AWS Setup
- Servlet and JS for main Movie page
- Demo'd Project
