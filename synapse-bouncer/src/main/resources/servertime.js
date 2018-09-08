var http = require("http")
var serverPort = 1338

http.createServer(function(req, res){
    res.writeHead(200, {'content-type' : 'text/plain'})
    res.end('welcome my fellow dev!\n')
}).listen(serverPort);

console.log('web server online @ ' + serverPort)
