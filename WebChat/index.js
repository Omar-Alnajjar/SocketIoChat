var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);

app.get('/', function(req, res){
  res.sendFile(__dirname + '/index.html');
});

io.on('connection', function(socket){
  console.log('a user connected');

  socket.on('chat message', function(msg, fn){
    fn('Acked');
    console.log('message: ' + msg);
    socket.broadcast.emit('chat message', msg);
  });

  socket.on('disconnect', function(){
    console.log('user disconnected');
  });

  socket.on('user joined', function(msg){
    console.log('joined: ' + msg['username']);
    socket.broadcast.emit('user joined', msg);
  });

  socket.on('user left', function(msg){
    console.log('left: ' + msg['username']);
    socket.broadcast.emit('user left', msg);
  });

  socket.on('typing', function(msg){
    console.log('typing: ' + msg);
    socket.broadcast.emit('typing', msg);
  });

  socket.on('stop typing', function(msg){
    console.log('stop typing: ' + msg);
    socket.broadcast.emit('stop typing', msg);
  });

});

http.listen(3000, function(){
  console.log('listening on **:3000');
});













