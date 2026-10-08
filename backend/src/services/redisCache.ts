import { redis } from './redis.js';
export async function cachedJson<T>(key:string,ttlSec:number,fn:()=>Promise<T>):Promise<T>{const hit=await redis.get(key);if(hit)return JSON.parse(hit) as T;const value=await fn();await redis.set(key,JSON.stringify(value),'EX',ttlSec);return value;}
export const cacheKey=(scope:string,input:string)=>`ai:${scope}:${Buffer.from(input).toString('base64url')}`;
