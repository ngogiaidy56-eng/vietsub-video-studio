import Redis from 'ioredis';
import { env } from '../config.js';
export const redis=new Redis(env.REDIS_URL,{maxRetriesPerRequest:null,enableReadyCheck:true});
redis.on('error',e=>console.error('redis',e));
