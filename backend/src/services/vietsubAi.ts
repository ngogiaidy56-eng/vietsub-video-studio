import {GoogleGenAI} from '@google/genai';
import {env} from '../config.js';
import {cachedJson,cacheKey} from './redisCache.js';
const ai=env.GEMINI_API_KEY?new GoogleGenAI({apiKey:env.GEMINI_API_KEY}):null;
export async function translateSubtitle(text:string,targetLanguage='vi'){if(!text.trim())return '';const key=cacheKey(`translate:${targetLanguage}`,text);return cachedJson(key,86400,async()=>{if(!ai)return `[${targetLanguage}] ${text}`;const r=await ai.models.generateContent({model:env.GEMINI_MODEL,contents:`Translate the following subtitle to ${targetLanguage}. Preserve meaning, timing friendliness, names, and natural spoken style. Return only the translation.\n\n${text}`,config:{temperature:0.2}});return (r.text??text).trim();});}
export async function translateBatch(items:{id:string;text:string}[],targetLanguage='vi'){const out=[];for(const item of items)out.push({...item,text:await translateSubtitle(item.text,targetLanguage)});return out;}
