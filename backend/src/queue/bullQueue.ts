import {Queue,QueueEvents} from 'bullmq';
import {redis} from '../services/redis.js';
export type JobType='render'|'translate'|'transcribe'|'ocr'|'extract';
export type JobPayload={jobId:string;type:JobType;userId:string;projectId?:string;inputPath?:string;outputPath?:string;subtitleFile?:string;text?:string;targetLanguage?:string;url?:string};
export const mediaQueue=new Queue<JobPayload>('vietsub-media',{connection:redis as any,defaultJobOptions:{attempts:4,backoff:{type:'exponential',delay:1500},removeOnComplete:100,removeOnFail:200}});
export const mediaEvents=new QueueEvents('vietsub-media',{connection:redis.duplicate() as any});
export async function enqueueMediaJob(payload:JobPayload){return mediaQueue.add(payload.type,payload,{jobId:payload.jobId});}
