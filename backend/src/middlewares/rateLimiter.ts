import type { RequestHandler } from 'express';
import { redis } from '../services/redis.js';
export const redisRateLimit=(limit=120,windowSec=60):RequestHandler=>async(req,res,next)=>{
  try{const key=`rl:${req.ip}:${Math.floor(Date.now()/1000/windowSec)}`; const n=await redis.incr(key); if(n===1) await redis.expire(key,windowSec+1); res.setHeader('X-RateLimit-Limit',String(limit)); res.setHeader('X-RateLimit-Remaining',String(Math.max(0,limit-n))); if(n>limit) return res.status(429).json({error:'rate_limited',retryAfter:windowSec}); next();}
  catch{next();}
};
