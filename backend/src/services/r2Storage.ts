import {S3Client,PutObjectCommand,GetObjectCommand} from '@aws-sdk/client-s3';
import {getSignedUrl} from '@aws-sdk/s3-request-presigner';
import {env} from '../config.js';
const client=env.R2_ENDPOINT&&env.R2_ACCESS_KEY_ID&&env.R2_SECRET_ACCESS_KEY?new S3Client({region:env.R2_REGION,endpoint:env.R2_ENDPOINT,credentials:{accessKeyId:env.R2_ACCESS_KEY_ID,secretAccessKey:env.R2_SECRET_ACCESS_KEY}}):null;
export async function createUploadUrl(key:string,contentType:string,expiresIn=900){if(!client)throw new Error('R2 is not configured');return getSignedUrl(client,new PutObjectCommand({Bucket:env.R2_BUCKET,Key:key,ContentType:contentType}),{expiresIn});}
export async function createDownloadUrl(key:string,expiresIn=900){if(!client)throw new Error('R2 is not configured');return getSignedUrl(client,new GetObjectCommand({Bucket:env.R2_BUCKET,Key:key}),{expiresIn});}
export function objectKey(userId:string,projectId:string,fileName:string){const safe=fileName.replace(/[^a-zA-Z0-9._-]/g,'_');return `users/${userId}/projects/${projectId}/${crypto.randomUUID()}-${safe}`;}
import crypto from 'node:crypto';
