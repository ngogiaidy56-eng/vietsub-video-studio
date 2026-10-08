import http from 'node:http';
const port=8799;
const server=http.createServer((req,res)=>{
  if(req.url==='/health'){res.writeHead(200,{'content-type':'application/json'});res.end(JSON.stringify({ok:true,mode:'dry-run'}));return;}
  res.writeHead(404);res.end();
});
server.listen(port,()=>console.log(`SOT sandbox on :${port}`));
