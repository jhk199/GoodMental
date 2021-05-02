# GoodMental

What I have in the app so far:
  I've been basically testing Riot's api and learning its faults. I've had to do several things to get it to work:
    
     1. I had to make an API proxy through AWS Lambda functions to hide my API key so Riot doesn't get mad
     2. I figured out how to make http async calls in Kotlin
     3. To get a player's match history, I have to search for their summoner name to get their encrypted account id to get their match list to get their match info
        a. Unfortunately, Match/v5 comes out in June, which would have made my life a lot easier
        
  In summary, my app is pretty bare bones. Right now, the api calls default to NA as I haven't implemented the spinner. Right now, you can input an NA summoner and get it print's a
  json of their most recent game. 
  
  Things I'm going to implement:
     
      Main screen to login with summoner name. It's saved
      Your summoner shows a list of recent matches and a nice message if you're winning/implicit intent to google maps parks if you're not
      Followed Summoners lets you save people or delete them and view their match history
      Journal to input stuff
      Database based in Firebase (I have Dan to help me, so that's good)
