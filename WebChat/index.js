var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var fs = require('fs');
var exec = require('child_process').exec;
var util = require('util');


var Files = {};

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


  socket.on('uploadFileStart', function (data) { //data contains the variables that 		we passed through in the html file
          var fileName = data['Name'];
          var fileSize = data['Size'];
          var username = data['username'];
          var Place = 0;
          var uploadFilePath = __dirname+'/' + fileName;

          console.log('uploadFileStart # Uploading file: %s to %s. Complete file size: %d', 		fileName, uploadFilePath, fileSize);

          socket.broadcast.emit('downloadStart', {'fileName':fileName, 'username':username});

          Files[fileName] = {  //Create a new Entry in The Files Variable
              FileSize    : fileSize,
              Data        : "",
              Downloaded  : 0
          }

          fs.open(uploadFilePath, "a", 0755, function(err, fd){
              if(err) {
                  console.log(err);
              }
              else {
                  console.log('uploadFileStart # Requesting Place: %d Percent %d', Place, 		0);

                  Files[fileName]['Handler'] = fd; //We store the file handler so we can 		write to it later
                  socket.emit('uploadFileMoreDataReq', { 'Place' : Place, 'Percent' : 0 });

                  // Send webclient upload progress..
              }
          });
      });

      socket.on('uploadFileChuncks', function (data){
          var Name = data['Name'];
          var base64Data = data['Data'];
          var playload = new Buffer(base64Data, 'base64').toString('binary');

          console.log('uploadFileChuncks # Got name: %s, received chunk size %d.', Name, 		playload.length);

          socket.broadcast.emit('downloadMore', base64Data);

          Files[Name]['Downloaded'] += playload.length;
          Files[Name]['Data'] += playload;

          if(Files[Name]['Downloaded'] == Files[Name]['FileSize']) //If File is Fully 		Uploaded
          {

              console.log('uploadFileChuncks # File %s receive completed', Name);
              socket.broadcast.emit('downloadComplete', true);
              fs.write(Files[Name]['Handler'], Files[Name]['Data'], null, 'Binary', 		function(err, Writen){
                 // close the file
                 fs.close(Files[Name]['Handler'], function() {
                    console.log('file closed');
                 });

                  // Notify android client we are done.
                  socket.emit('uploadFileCompleteRes', { 'IsSuccess' : true });

                  // Send the Webclient the path to download this file.

              });
          }
          else if(Files[Name]['Data'].length > 10485760){ //If the Data Buffer reaches 10MB
              console.log('uploadFileChuncks # Updating file %s with received data', Name);

              fs.write(Files[Name]['Handler'], Files[Name]['Data'], null, 'Binary', 		function(err, Writen){
                  Files[Name]['Data'] = ""; //Reset The Buffer
                  var Place = Files[Name]['Downloaded'];
                  var Percent = (Files[Name]['Downloaded'] / Files[Name]['FileSize']) * 100;

                  socket.emit('uploadFileMoreDataReq', { 'Place' : Place, 'Percent' :  		Percent});

                  // Send webclient upload progress..

              });
          }
          else
          {
              var Place = Files[Name]['Downloaded'];
              var Percent = (Files[Name]['Downloaded'] / Files[Name]['FileSize']) * 100;
              console.log('uploadFileChuncks # Requesting Place: %d, Percent %s', Place, 		Percent);

              socket.emit('uploadFileMoreDataReq', { 'Place' : Place, 'Percent' :  		Percent});
              // Send webclient upload progress..
          }
      });

});

http.listen(3000, function(){
  console.log('listening on **:3000');
});













