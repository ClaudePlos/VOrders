<%-- 
    Document   : login
    Created on : 2011-12-01, 16:39:57
    Author     : Piotrek
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html >
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Logowanie do systemu iNaprzód</title>
        <style type="text/css">
            
               #outer {height: 100%; overflow: hidden; position: relative;}
                #outer[id] {display: table; position: static;}
		
		#middle {position: absolute; top: 50%;} /* for explorer only*/
		#middle[id] {display: table-cell; vertical-align: middle; width: 100%; text-align: center}
		
		#inner {position: relative; top: -50%;text-align: center} /* for explorer only */
		/* optional: #inner[id] {position: static;} */
		
		
                
                
                label{
                    float: left;
                    width: 100px;
                    font-weight: normal;
                    font-family : verdana;
                    text-align: right;
                    font-size: 14px; 
                    vertical-align: bottom; 
                    display: block; 
                    padding-top: 9px;
                }

                    input, textarea{
                        width: 300px;
                        margin-bottom: 10px;
                        font-family: verdana;
                        font-size:  20px; 
                    }

                    .boxes{
                    width: 1em;
                    }
                    
                    #submitbutton{
                    margin-left: 120px;
                    margin-top: 5px;
                    width: 90px;
                    }

                    br
                    {
                        clear: left;
                    }
                    
	</style>
        <!--<link rel="icon" type="image/x-icon" href="favicon.ico"/>-->
    </head>
    
    
    <!--
    <H1 style="font-family:verdana" >
            <CENTER>ZostaĹ‚eĹ› poprawnie wylogowany ze strony iNaprzĂłd.</CENTER>
        </H1> -->
    
    <body style="margin:100px 0px; padding:0px;text-align:center;" >
        
     <!--  <div id="outer">
    
            <div id="middle">
                <div id="inner" > -->
                    
                    <!--<img src="/logo6.png" />-->
                    
                    <div style="margin:0px auto;width:450px;font-family: verdana;font-size: 14px;font-weight: bold;" >
                        
                        Logowanie do systemu zamówień<br/><br/>
                        
                     <%  
                        if( request.getRequestURI().equals("/j_security_check"))
                        {  
                             out.print(request.getAttribute("Błąd - podane hasło nie jest poprawne")) ;
                        }  
                        else  
                        {  
                             
                        }  
                        %>
                        <form action="j_security_check" method="POST" style="font-family:verdana;vertical-align: bottom">
                            
                            <label for="user">Użytkownik:</label>
                            <input type="text" name="j_username">
                            <br/>

                            <label for="haslo">Hasło:</label>
                            <input type="password" name="j_password"><br/>
                           <input type="submit" value="Zaloguj">
                           
                        </form>
                        
                    </div>
                <!--</div>
                
            </div>
        
        </div>-->
            
    </body>
</html>


        
        
</BODY>

</HTML>
