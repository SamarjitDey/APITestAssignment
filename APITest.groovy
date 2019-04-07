  
/** 
* This test has been written in Groovy to illustrate test automation framework for any API 
* that supports validation of node values for two types
* 1. Parent Nodes
* 2. Parent.Child Nodes
* The APITest program implements an automation test that reads dataSource from testData file/folder
* and extracts values into a @map = listOfTestData and validates each item(node) inside it.
* The progam runs from command line and validation messages are shown there. Exceptions would exit
* the program or either end in unsuccessful run. Such messages would also be displayed in command line.
* On successful test run, a test report.html should be auto-triggered and displayed with default system 
* browser on Windows operating system.
* 
* 
* author  Samarjit Dey 
* version 1.1 
* since   06-Apr-2018 
*/

import groovy.json.*
import java.io.File
import java.awt.Desktop

// Returns default location of the framework
def folderLocation = System.getProperty("user.dir");

try{
    // Get test Data contents 
    def dataSource = new File("$folderLocation/TestData/testData.txt").getText('UTF-8')
    def getURI, getResponseParsed
    
    def listOfTestData = [:]
    def htmlRows= []
    
    // Traverse each lines inside dataSource 
    dataSource.eachLine{
        if(it.contains("=") && !it.contains("contains")){
            if(it.contains("API")){
                try{
                    /** Extract API and get response with response Code
                    *    Check if code is other than 200. If bad request or null,
                    *    exit the program with proper error message
                    */
                    getURI = it.split("=")[1].trim()
                    getResponseParsed = (getApiResponse(getURI) == "Bad Request" || null) ? 2/0 : getApiResponse(getURI)
                }catch(Exception e){
                    println "Incorrect API. Bad Request. Exiting program..."
                    System.exit(0)
                }
            }else
            listOfTestData.put("${it.split("=")[0].trim()}","${it.split("=")[1].trim()}") // Extracts parent node as key and data as value and save in map
       }else if(it.contains("contains")){
            // Extracts parent.child node as key and data as value and save in map 
			listOfTestData.put("${it.split("and")[0].trim()}","${it.split("and")[1].trim()}") 
        }
    }
    
    // This function would return map when any node is searched in a json
    jsonParse = { field, json ->
        field.tokenize(".").inject(json) { map, f -> map[f] }
    }
    
    if(!listOfTestData.isEmpty()){
        listOfTestData.each{ item->
            validateNodes(item, getResponseParsed, htmlRows) // Validation of each node extracted from listOfTestData
        }
    }else if(listOfTestData.isEmpty()){
        println "Test Data not available. Exiting program..." // Exiting if listOfTestData found empty
        System.exit(0)
    }
    
    if(!htmlRows.isEmpty()){
        createReport(htmlRows, folderLocation, getURI) // Generate test report for the acceptance criteria and save it in report folder
    }else{
        println "Cannot generate Report. Please see the results in the console"
    }
}catch(Exception e){
    println "Unsuccessful Test Run: $e. Exiting program..." // Any unforseen event, should exit the program
    System.exit(0)
}
finally{
    println "Test Execution Complete..." // On successful test run completion
}

/** Method to hit API and parse response only on (responseCode = 200), else Bad request
* In case, program is not able to send request or any MalformedURIException occurs, program should
* catch exception, print it and return null
*/
def getApiResponse(def URI){
    def apiResponse, apiResponseParsed, apiResponseCode
        try{
            apiResponse = "$URI".toURL().getText('UTF-8')
            apiResponseCode = "$URI".toURL().openConnection().responseCode
            apiResponseParsed = (apiResponseCode == 200) ? new JsonSlurper().parseText(apiResponse) : "Bad Request"
        }catch(Exception e){
            println "Error getting  response : $e"
            apiResponseParsed = null
        }
    return apiResponseParsed
}

/** Method to validate each node per acceptance criteria 
* Parameters are items in the listOfTestData, parsed reponse from API and htmlRows to track rows for html report
*/
def validateNodes(def item, def getResponseParsed, def htmlRows){

    try{
        def targetKey, sourceVal, targetVal // Variables for parent node validations
        def targetKeyA, targetKeyB, sourceValA, sourceValB, targetValA, targetValB, flagA, flagB // Variables for parent.child node validations
        
        // To extract key and values for from parent.child key extracted above
        if(item.key.toString().contains(".")){
            if(item.key.contains("=")){
                targetKeyA = item.key.split("=")[0].trim()
                sourceValA = item.key.split("=")[1].trim()
                flagA = 1
            }else if(item.key.contains("contains")){
                targetKeyA = item.key.split("contains")[0].trim()
                sourceValA = item.key.split("contains")[1].trim()
                flagA = 1
            }
        }else
            targetKey = item.key
              
        // To extract key and values for from parent.child value extracted above
        if(item.value.toString().contains(".")){
            if(item.value.contains("=")){
                targetKeyB = item.value.split("=")[0].trim()
                sourceValB = item.value.split("=")[1].trim()
                flagB = 1
            }else if(item.value.contains("contains")){
                targetKeyB = item.value.split("contains")[0].trim()
                sourceValB = item.value.split("contains")[1].trim()
                flagB = 1
            }
        }else
            sourceVal = item.value
       
        // Validation block for parent.child node acceptance criteria
        if(flagA == 1 && flagB == 1){
            sourceValA = sourceValA.matches("\".*\"") ? sourceValA.replaceAll("\"", "") : sourceValA // Replace any start-end quotes with blank
            sourceValB = sourceValB.matches("\".*\"") ? sourceValB.replaceAll("\"", "") : sourceValB

            /**since acceptance criteia validation has "and" clause. Therefore, it checks if both keys have same parent
            * here targetKey(A or B).split("\\.")[0] will mean parent and targetKey(A or B).split("\\.")[1] will mean child
            * If both are same, traverse through each child in the parent map
            */
            getResponseParsed.findAll{(targetKeyA.split("\\.")[0]) == (targetKeyB.split("\\.")[0])}.(targetKeyA.split("\\.")[0]).each{ node->

                // Checks if child key exists in parent instance
                // If not found, report node not found error and exit the program
                targetValA = node.containsKey(targetKeyA.split("\\.")[1]) ? node.(targetKeyA.split("\\.")[1]) : 2/0
                targetValB = node.containsKey(targetKeyB.split("\\.")[1]) ? node.(targetKeyB.split("\\.")[1]) : 2/0

                // Check if the child value matches/contains with source value in test data
                if(targetValA.toString() == sourceValA.toString() && targetValB.toString().contains(sourceValB.toString())){
                    println "Passed: $targetKeyA equals to $sourceValA and $targetKeyB contains $sourceValB"
                    
                    htmlRows.add("$targetKeyA-$sourceValA-$targetValA-Passed") // Logs each key, value and pass to be reported in html report
                    htmlRows.add("${targetKeyB} (contains)-$sourceValB-$targetValB-Passed") // Logs each key, value and pass to be reported in html report
                }else if(targetValA.toString().contains(sourceValA.toString()) && targetValB.toString() == sourceValB.toString()){
                    println "Passed: $targetKeyA contains $sourceValA and $targetKeyB equals to $sourceValB"
                   
                    htmlRows.add("${targetKeyA} (contains)-$sourceValA-$targetValA-Passed")
                    htmlRows.add("$targetKeyB-$sourceValB-$targetValB-Passed")
                }else if(targetValA.toString() == sourceValA.toString() && !(targetValB.toString().contains(sourceValB.toString()))){
                    println "Failed: $targetKeyA equals to $sourceValA but $targetKeyB does not contains $sourceValB"
                   
                    htmlRows.add("$targetKeyA-$sourceValA-$targetValA-Failed") // Logs each key, value and fail to be reported in html report
                    htmlRows.add("${targetKeyB} (contains)-$sourceValB-$targetValB-Failed") // Logs each key, value and fail to be reported in html report
                }else if(targetValA.toString().contains(sourceValA.toString()) && targetValB.toString() != sourceValB.toString()){
                    println "Passed: $targetKeyA contains $sourceValA but $targetKeyB does not equals to $sourceValB"
                    
                    htmlRows.add("${targetKeyA} (contains)-$sourceValA-$targetValA-Failed")
                    htmlRows.add("$targetKeyB-$sourceValB-$targetValB-Failed")
                }
             }   
        }
        
        else{
            sourceVal = item.value.matches("\".*\"") ? item.value.replaceAll("\"", "") : item.value // Replace any start-end quotes with blank
            
            // Checks if child key exists in parent instance
            // If not found, report node not found error and exit the program
            targetVal = getResponseParsed.containsKey(targetKey.toString()) ? jsonParse(targetKey, getResponseParsed) : 2/0
                        
            if(targetVal instanceof List){
                targetVal = targetVal[0] // If jsonParse return map, take value inside it
            }
            if(sourceVal instanceof List){
                sourceVal = sourceVal[0]
            }
        

            // Check if the child value matches/contains with source value in test data        
            if(sourceVal.toString() == targetVal.toString()){
                println "Passed: $targetKey has value as $sourceVal"
                htmlRows.add("$targetKey-$sourceVal-$targetVal-Passed") // Logs each key, value and pass to be reported in html report
            }else{
                println "Failed: $targetKey does not has value as $sourceVal"
                htmlRows.add("$targetKey-$sourceVal-$targetVal-Failed") // Logs each key, value and fail to be reported in html report
            }
        }
    }catch(ArithmeticException e){
            println "Exception: Node doesn't exists. Exiting program..." // Report if node does not exists and exit the program
            System.exit(0)
        }
     catch(Exception e){
            println "Exception: $e. Exiting program..." //If unforseen event occurs, exit the program
            System.exit(0)
        }
}

/** Method to generate report based on acceptance criteria and save in report location of the framework
* In case, program is not able to send request or any MalformedURIException occurs, program should
* catch exception, print it and return null
*/
def createReport(def htmlRows, def folderLocation, def getURI){
    println "\n\nGenerating Report Html file....."
    htmlReport = new File("$folderLocation/Reports/Report.html") //Create html report file
    if(htmlReport.exists()){
       htmlReport.delete() // Delete if file already exists in folder location
    }
    StringBuilder buffer = new StringBuilder() // To create script for HTML table 
    
    // HTML script for the static start part
    def htmlStart = '''<!DOCTYPE html>
                        <!DOCTYPE HTML PUBLIC 
                        http://www.w3.org/TR/1999/REC-html401-19991224/loose.dtd>
                        <HTML>
                        <head>
                        <META HTTP-EQUIV=Content-Type
                        CONTENT=text/html; charset=utf-8>
                        <style type=text/css>
                        .tftable {font-size:12px;color:#333333;border-width: 1px;border-color: #800000;border-collapse: collapse;table-layout: fixed;}
                        .tftable th {font-size:12px;background-color:#f79646;border-width: 1px;padding: 8px;border-style: solid;border-color: #800000;text-align:center;word-wrap: break-word;}
                        .tftable tr:nth-child(odd) {background-color:#87CEFA;}
                        .tftable tr:nth-child(even) {background-color:#B0E0E6;}
                        .tftable td {font-size:12px;border-width: 1px;padding: 8px;border-style: solid;border-color: #800000; text-align:center;word-wrap: break-word;}
                        .tftable tr:hover {background-color:#ffffff;}
                        body{font-family: Trebuchet MS, Helvetica, sans-serif;}
                        </style>
                        </head>
                        <body>

                        <table align="center">
                          <tr>
                            <th> Test Automation Report for API </th>
                        </tr>
                        </table>
                        <table class=tftable border=1 align="center">
                         <tr>
                            <th> Sl. No. </th>
                            <th> Nodes in Acceptance Criteria </th>
                            <th> Expected Value </th>
                            <th> Actual Value </th>
                            <th> Status </th>
                            
                          </tr>''' 

    buffer.append htmlStart // Append static part in buffer string

    // HTML script for the static end part
    def  htmlEnd = '''
    </table>
    <table align="center">
    <tr>
        <th> API in Test : <a href = '''+ getURI +''' target="_blank"> Link </a></th>
    </tr>
    </table>
    </body>
    </html>'''

    def counter = 1 // Generates serial number
    htmlRows.each{
        buffer.append "<tr> <td> $counter </td>" // Aappending html table rows into string
        it.split("-").each{ val->
            buffer.append "<td> $val </td>" // Appending html table rows into string
        }
        buffer.append "</tr>"
        counter++
    } 
    buffer.append htmlEnd // Append html static end part in buffer string
    htmlReport.append buffer // Writing string buffer into html file
    sleep(2000)  
    println "Opening Report Html file....."
    Desktop.getDesktop().browse(htmlReport.toURI()); // Open html file with default browser of the system
}