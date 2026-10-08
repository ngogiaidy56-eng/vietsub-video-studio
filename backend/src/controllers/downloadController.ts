import type {Request,Response} from 'express';
import {createDownloadUrl} from '../services/r2Storage.js';
export async function downloadController(req:Request,res:Response){const key=String(req.query.key??'');if(!key)return res.status(400).json({error:'key_required'});res.json({url:await createDownloadUrl(key)});}
