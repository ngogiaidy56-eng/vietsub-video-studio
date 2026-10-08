import {spawn} from 'node:child_process';
import fs from 'node:fs/promises';
import path from 'node:path';
import {log} from '../utils/logger.js';
export type RenderProgress=(p:number,stage:string)=>Promise<void>|void;
export async function renderWithFfmpeg(input:string,output:string,subtitleFile:string|undefined,onProgress:RenderProgress){await fs.mkdir(path.dirname(output),{recursive:true});const args=['-hide_banner','-y','-i',input];if(subtitleFile)args.push('-vf',`subtitles=${subtitleFile.replaceAll('\\','/')}`);args.push('-c:v','libx264','-preset','medium','-crf','20','-c:a','aac','-movflags','+faststart',output);await new Promise<void>((resolve,reject)=>{const child=spawn(process.env.FFMPEG_PATH??'ffmpeg',args,{stdio:['ignore','ignore','pipe']});let stderr='';child.stderr.on('data',d=>{stderr+=d.toString();});const timer=setInterval(()=>onProgress(50,'ffmpeg'),1000);child.on('error',e=>{clearInterval(timer);reject(e)});child.on('close',code=>{clearInterval(timer);if(code===0)resolve();else{log.error('ffmpeg failed',{stderr:stderr.slice(-4000)});reject(new Error(`ffmpeg_exit_${code}`));}})});await onProgress(100,'completed');}
