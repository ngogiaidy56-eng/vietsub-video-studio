import {translateSubtitle} from './vietsubAi.js';
export async function translateImageText(text:string,target='vi'){return {sourceText:text,targetText:await translateSubtitle(text,target)}}
