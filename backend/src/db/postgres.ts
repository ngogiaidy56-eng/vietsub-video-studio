import pg from 'pg';
import {env} from '../config.js';
const {Pool}=pg;
export const pgPool=process.env.DATABASE_URL?new Pool({connectionString:process.env.DATABASE_URL,max:10}):null;
export async function initDatabase(){if(!pgPool)return;await pgPool.query(`CREATE TABLE IF NOT EXISTS media_jobs(id UUID PRIMARY KEY,user_id TEXT NOT NULL,project_id TEXT,type TEXT NOT NULL,status TEXT NOT NULL,progress INTEGER NOT NULL DEFAULT 0,payload JSONB NOT NULL,result JSONB,error TEXT,created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW())`)}
export async function saveJob(job:any){if(!pgPool)return;await pgPool.query(`INSERT INTO media_jobs(id,user_id,project_id,type,status,payload) VALUES($1,$2,$3,$4,$5,$6::jsonb) ON CONFLICT(id) DO UPDATE SET status=excluded.status,payload=excluded.payload,updated_at=NOW()`,[job.id,job.userId,job.projectId??null,job.type,job.status,JSON.stringify(job.payload??{})])}
export async function updateJob(id:string,patch:{status?:string;progress?:number;result?:unknown;error?:string}){if(!pgPool)return;await pgPool.query(`UPDATE media_jobs SET status=COALESCE($2,status),progress=COALESCE($3,progress),result=COALESCE($4::jsonb,result),error=COALESCE($5,error),updated_at=NOW() WHERE id=$1`,[id,patch.status??null,patch.progress??null,patch.result?JSON.stringify(patch.result):null,patch.error??null])}
