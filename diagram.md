```plantuml
@startUML

actor Client
participantgroup #lightgray :0 **Chess Server**
participantgroup #gray :0 
participant Server
end
participantgroup #gray :0 Services 
participant RegistrationService
participant LoginService
participant GameService
end
participantgroup #gray :0
participant DataAccess
end
end
database db

group #navy Registration #white
Client -> Server: [POST] /user\n{username, password, email}
Server -> RegistrationService: register(username, password, email)
RegistrationService -> DataAccess: getUser(username)
DataAccess -> db: SELECT username FROM user
DataAccess --> RegistrationService: null
RegistrationService -> DataAccess: createUser(username, password)
DataAccess -> db: INSERT username, password, email INTO user
RegistrationService -> DataAccess: createAuth(username)
DataAccess -> db: INSERT username, authToken INTO auth
DataAccess --> RegistrationService: authToken
RegistrationService --> Server: authToken
Server --> Client: 200\n{authToken}
end

group #orange Login #white
Client -> Server: [POST] /session\n{username, password}
Server -> LoginService: login(username, password)
LoginService -> DataAccess: getUser(username)
DataAccess -> db: SELECT username, password FROM user
DataAccess --> LoginService: (user, password)
LoginService <<- LoginService: verifyPassword(user.password, password) # Checks password
LoginService -> DataAccess: createAuth(username) # Creates new authToken
DataAccess -> db: INSERT username, authToken INTO auth
DataAccess --> LoginService: authToken
LoginService --> Server: authToken
Server --> Client: 200\n{authToken}
end

group #green Logout #white
Client -> Server: [DELETE] /session\nauthToken
Server -> LoginService: logout(authToken)
LoginService -> DataAccess: deleteAuth(authToken)
DataAccess -> db: DELETE FROM auth WHERE authToken = <authToken>
DataAccess --> LoginService: true/false # Success/Failure
LoginService --> Server: true/false # If successful
Server --> Client: 200
end

group #red List Games #white
Client -> Server: [GET] /game\nauthToken
Server -> GameService: verifyAuthToken(authToken) # Verify auth token
GameService -> DataAccess: verifyAuthToken(authToken) # Verify auth token
DataAccess -> db: SELECT userID FROM auth WHERE authToken = <authToken>
DataAccess --> GameService: userID
GameService -> DataAccess: listGames()
DataAccess -> db: SELECT gameID, whiteUsername, blackUsername, gameName FROM game
DataAccess --> GameService: list(games)
GameService --> Server: list(games)
Server --> Client: 200\n{"games": list(games)}
end

group #purple Create Game #white
Client -> Server: [POST] /game\nauthToken\n{gameName}
Server -> GameService: verifyAuthToken(authToken) # Verify auth token
GameService -> DataAccess: verifyAuthToken(authToken) # Verify auth token
DataAccess -> db: SELECT userID FROM auth WHERE authToken = <authToken>
DataAccess --> GameService: userID
GameService -> DataAccess: createGame(gameName)
DataAccess -> db: INSERT gameName, creatorUsername INTO game
DataAccess --> GameService: gameID
GameService --> Server: gameID
Server --> Client: 200\n{gameID}
end

group #yellow Join Game #black
Client -> Server: [PUT] /game\nauthToken\n{ClientColor, gameID}
Server -> GameService: verifyAuthToken(authToken) # Verify auth token
GameService -> DataAccess: verifyAuthToken(authToken) # Verify auth token
DataAccess -> db: SELECT userID FROM auth WHERE authToken = <authToken>
DataAccess --> GameService: userID
GameService -> DataAccess: getGame(gameID)
DataAccess -> db: SELECT * FROM game WHERE gameID = <gameID>
DataAccess --> GameService: game
GameService <<- GameService: validateJoin(game, clientColor) # Checks game validity and color availability
GameService -> DataAccess: updateGameWithUserColor(game, clientColor, authToken) # Update game with user's color and auth token
DataAccess -> db: UPDATE game SET ClientColor = <clientColor> WHERE gameID = <gameID> AND authToken = <authToken>
DataAccess --> GameService: true/false # Success/Failure
GameService --> Server: true/false # If successful
Server --> Client: 200
end

group #gray Clear application #white
Client -> Server: [DELETE] /db
Server -> RegistrationService: clear()
RegistrationService -> DataAccess: clear()
DataAccess -> db: DELETE FROM user, game, auth
DataAccess --> RegistrationService: true/false # Success/Failure
RegistrationService --> Server: 200 # If successful
Server --> Client: 200
end

@enduml
```