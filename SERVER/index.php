<?php
/****************************************
SPEECHLANGUAGE
Author: Cindy Espínola
*************************************/

require_once("mysql.class.php");
$dbHost = "localhost";
$dbUsername = "DBUSERNAME";
$dbPassword = "DBPASSWORD";
$dbName = "DBNAME";

$db = new MySQL($dbHost,$dbUsername,$dbPassword,$dbName);

// if operation is failed by unknown reason
define("FAILED", 0);
define("SUCCESSFUL", 1);
// when  signing up, if username is already taken, return this error
define("SIGN_UP_USERNAME_CRASHED", 2);  
// when add new friend request, if friend is not found, return this error 
define("ADD_NEW_USERNAME_NOT_FOUND", 2);

// TIME_INTERVAL_FOR_USER_STATUS: if last authentication time of user is older 
// than NOW - TIME_INTERVAL_FOR_USER_STATUS, then user is considered offline
define("TIME_INTERVAL_FOR_USER_STATUS", 60);
define("USER_APPROVED", 1);
define("USER_UNAPPROVED", 0);


$username = (isset($_REQUEST['username']) && count($_REQUEST['username']) > 0) 
  						? $_REQUEST['username'] 
							: NULL;
$password = isset($_REQUEST['password']) ? md5($_REQUEST['password']) : NULL;

$port = isset($_REQUEST['port']) ? $_REQUEST['port'] : NULL;

$action = isset($_REQUEST['action']) ? $_REQUEST['action'] : NULL;
if ($action == "testWebAPI")
{
	if ($db->testconnection()){
	echo SUCCESSFUL;
	exit;
	}else{
	echo FAILED;
	exit;
	}
}

if ($username == NULL || $password == NULL)	 
{
	echo FAILED;
	exit;
}

$out = NULL;

//error_log($action."\r\n", 3, "error.log");
switch($action) 
{
	
	case "authenticateUser":
		
		
		if ($userId = authenticateUser($db, $username, $password)) {					
			$sql = "select u.Id, u.username, (NOW()-u.authenticationTime) as authenticateTimeDifference, u.IP, 
										f.providerId, f.requestId, f.status, u.port 
							from friends f
							left join users u on 
										u.Id = if ( f.providerId = ".$userId.", f.requestId, f.providerId ) 
							where (f.providerId = ".$userId." and f.status=".USER_APPROVED.")  or 
										 f.requestId = ".$userId." ";
										 
			//$sqlmessage = "SELECT * FROM `messages` WHERE `touid` = ".$userId." AND `read` = 0 LIMIT 0, 30 ";
			
			$sqlmessage = "SELECT m.id, m.fromuid, m.touid, m.sentdt, m.read, m.readdt, m.messagetext, u.username from messages m \n"
    . "left join users u on u.Id = m.fromuid WHERE `touid` = ".$userId." AND `read` = 0 LIMIT 0, 30 ";
			
	
			if ($result = $db->query($sql))			
			{
					$out .= "<data>"; 
					$out .= "<user userKey='".$userId."' />";
					while ($row = $db->fetchObject($result))
					{
						$status = "offline";
						if (((int)$row->status) == USER_UNAPPROVED)
						{
							$status = "unApproved";
						}
						else if (((int)$row->authenticateTimeDifference) < TIME_INTERVAL_FOR_USER_STATUS)
						{
							$status = "online";
							 
						}
						$out .= "<friend  username = '".$row->username."'  status='".$status."' IP='".$row->IP."' userKey = '".$row->Id."'  port='".$row->port."'/>";
												
												// to increase security, we need to change userKey periodically and pay more attention
												// receiving message and sending message 
						
					}
						if ($resultmessage = $db->query($sqlmessage))			
							{
							while ($rowmessage = $db->fetchObject($resultmessage))
								{
								$out .= "<message  from='".$rowmessage->username."'  sendt='".$rowmessage->sentdt."' text='".$rowmessage->messagetext."' />";
								$sqlendmsg = "UPDATE `messages` SET `read` = 1, `readdt` = '".DATE("Y-m-d H:i")."' WHERE `messages`.`id` = ".$rowmessage->id.";";
								$db->query($sqlendmsg);
								}
							}
					$out .= "</data>";
			}
			else
			{
				$out = FAILED;
			}			
		}
		else
		{
				// exit application if not authenticated user
				$out = FAILED;
		}
		
	
	
	break;
	
	//var_dump($_REQUEST);
	
	case "signUpUser":
		if (isset($_REQUEST['email'])){
			 $email = $_REQUEST['email'];		
			 $language= $_REQUEST['language'];
			 $sql = "select Id from  users where username = '".$username."' limit 1";				
			 if ($result = $db->query($sql)) {
			 		if ($db->numRows($result) == 0) {
							$sql = "insert into users(username, password, email,language)
			 					values ('".$username."', '".$password."', '".$email."','".$language."') ";		 					
						 					
			 				if ($db->query($sql)){
							 		$out = SUCCESSFUL;
							}else {
									$out = FAILED;
							}				 			
			 		}else{
			 			$out = SIGN_UP_USERNAME_CRASHED;
			 		}
			 }				 	 	
		}else{
			$out = FAILED;
		}	
	break;
	
	///////////////////////////////////////////////////
	case "textTranslate":
	
	//http://localhost/android_im/index.php?username=cinesme&password=12345&action=textTranslate&from=en&to=es&text=hello
			$text= $_REQUEST['text'];
			$from = $_REQUEST['from'];
			$to = $_REQUEST['to'];		
			//$outStr = "";
			 
			//$outStr= translate($toLanguage,$fromLanguage,$inputStr);
						
			//$text="planet is alone";
			//$from = 'en';
			//$to = 'es';
			
			$out=translate($from,$to,$text);
			//echo translate("en","es","dog");
			//$out=$outStr;
		
			//$out=$outStr;
			
	break;
	//////////////////////////////////////////////////
	
	
	case "sendMessage":
	if ($userId = authenticateUser($db, $username, $password)) 	{	
		if (isset($_REQUEST['to']))	{
			 $tousername = $_REQUEST['to'];	
			 $message = $_REQUEST['message'];	
 //http://localhost/android_im/index.php?username=cinesme&password=12345&action=sendMessage&to=android&message=hello android
			 $sqlto = "select Id,language from  users where username = '".$tousername."' limit 1";
					if ($resultto = $db->query($sqlto)){
						while ($rowto = $db->fetchObject($resultto)){
							$uto = $rowto->Id;
							$toLanguage= $rowto->language;
						}
						$fromLanguage=getLanguageUser($db, $username, $password);
						$message=translate($fromLanguage,$toLanguage,$message);

		$sql22 = "INSERT INTO `messages` (`fromuid`, `touid`, `sentdt`, `messagetext`) VALUES ('".$userId."', '".$uto."', '".DATE("Y-m-d H:i")."', '".$message."');";									
			 					error_log("$sql22", 3 , "error_log");
							if ($db->query($sql22))	{
							 		$out = SUCCESSFUL;
							}				
							else {
									$out = FAILED;
							}				 		
						$resultto = NULL;
						echo $message;
						exit;
					}				 	 	
		$sqlto = NULL;
		}
		}else{
			$out = FAILED;
		}	
	break;
	
	case "addNewFriend":
		$userId = authenticateUser($db, $username, $password);
		if ($userId != NULL)
		{
			
			if (isset($_REQUEST['friendUserName']))	{				
				 $friendUserName = $_REQUEST['friendUserName'];
				 
				 $sql = "select Id from users where username='".$friendUserName."'limit 1";
				 if ($result = $db->query($sql)) {
				 		if ($row = $db->fetchObject($result))	{
				 			 $requestId = $row->Id;
				 			 
				 			 if ($row->Id != $userId) {
				 			 		 $sql = "insert into friends(providerId, requestId, status)
				 				  		 values(".$userId.", ".$requestId.", ".USER_UNAPPROVED.")";
							 
									 if ($db->query($sql)) {
									 		$out = SUCCESSFUL;
									 }
									 else
									 {
									 		$out = FAILED;
									 }
							}
							else
							{
								$out = FAILED;  // user add itself as a friend
							} 		 				 				  		 
				 		}
				 		else
				 		{
				 			$out = FAILED;			 			
				 		}
				 }				 				 
				 else
				 {
				 		$out = FAILED;
				 }				
			}
			else
			{
					$out = FAILED;
			} 			
		}
		else
		{
			$out = FAILED;
		}	
	break;
	
	case "responseOfFriendReqs":
		$userId = authenticateUser($db, $username, $password);
		if ($userId != NULL)
		{
			$sqlApprove = NULL;
			$sqlDiscard = NULL;
			if (isset($_REQUEST['approvedFriends']))
			{
				  $friendNames = split(",", $_REQUEST['approvedFriends']);
				  $friendCount = count($friendNames);
				  $friendNamesQueryPart = NULL;
				  for ($i = 0; $i < $friendCount; $i++)
				  {
				  	if (strlen($friendNames[$i]) > 0)
				  	{
				  		if ($i > 0 )
				  		{
				  			$friendNamesQueryPart .= ",";
				  		}
				  		
				  		$friendNamesQueryPart .= "'".$friendNames[$i]."'";
				  		
				  	}			  	
				  	
				  }
				  if ($friendNamesQueryPart != NULL)
				  {
				  	$sqlApprove = "update friends set status = ".USER_APPROVED."
				  					where requestId = ".$userId." and 
				  								providerId in (select Id from users where username in (".$friendNamesQueryPart."));
				  				";		
				  }
				  				  
			}
			if (isset($_REQUEST['discardedFriends']))
			{
					$friendNames = split(",", $_REQUEST['discardedFriends']);
				  $friendCount = count($friendNames);
				  $friendNamesQueryPart = NULL;
				  for ($i = 0; $i < $friendCount; $i++)
				  {
				  	if (strlen($friendNames[$i]) > 0)
				  	{
				  		if ($i > 0 )
				  		{
				  			$friendNamesQueryPart .= ",";
				  		}
				  		
				  		$friendNamesQueryPart .= "'".$friendNames[$i]."'";
				  		
				  	}				  	
				  }
				  if ($friendNamesQueryPart != NULL)
				  {
				  	$sqlDiscard = "delete from friends where requestId = ".$userId." and providerId in (select Id from users where username in (".$friendNamesQueryPart."));";
				  }						
			}
			if (  ($sqlApprove != NULL ? $db->query($sqlApprove) : true) &&
						($sqlDiscard != NULL ? $db->query($sqlDiscard) : true) 
			   )
			{
				$out = SUCCESSFUL;
			}
			else
			{
				$out = FAILED;
			}		
		}
		else
		{
			$out = FAILED;
		}
	break;
	
	default:
		$out = FAILED;		
		break;	
}

echo $out;

///////////////////////////////////////////////////////////////
function authenticateUser($db, $username, $password){
	$sql22 = "select * from users where username = '".$username."' and password = '".$password."'limit 1";
	$out = NULL;
	if ($result22 = $db->query($sql22)){
		if ($row22 = $db->fetchObject($result22)){
				$out = $row22->Id;
				$sql22 = "update users set authenticationTime = NOW(), IP = '".$_SERVER["REMOTE_ADDR"]."' ,port = 15145 where Id= ".$row22->Id."limit 1";
				$db->query($sql22);									
		}		
	}
	return $out;
}
///////////////////////////////////////////////////////////////
function getLanguageUser($db, $username, $password){
	
	$sql22 = "select language from users where username = '".$username."' and password = '".$password."'limit 1";
	
	$out = NULL;
	if ($result22 = $db->query($sql22))
	{
		if ($row22 = $db->fetchObject($result22))
		{
				$out = $row22->language;
	
		}		
	}
	
	return $out;
}
//8888888888888888888888888888888888888888888 FUNCTION TRANSLATE 88888888888888888888888888888888888888888
//function lang($db, $username, $password){
//$sql23 = "select language from users where username = '".$username."' and password = '".$password."'limit 1";
//$lan = $sql23;
//return $lan;
//}
/////////////////
function translate($from, $to, $text){
 $key = 'yrTy+5YhqVWA5f3akiWegWKtrlxO86EsrqwNzSS+Lpw';
$ch = curl_init('https://api.datamarket.azure.com/Bing/MicrosoftTranslator/v1/Translate?Text=%27'.urlencode($text).'%27&From=%27'.$from.'%27&To=%27'.$to.'%27');
curl_setopt($ch, CURLOPT_USERPWD, $key.':'.$key);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
$result = curl_exec($ch);
$result = explode('<d:Text m:type="Edm.String">', $result);
$result = explode('</d:Text>', $result[1]);
$result = $result[0];
return $result;

}

?>
