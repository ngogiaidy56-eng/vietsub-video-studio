import fs from 'node:fs/promises';
export type TranscriptSegment={startMs:number;endMs:number;text:string};
export async function transcribeVideo(filePath:string):Promise<TranscriptSegment[]>{await fs.stat(filePath); return [{startMs:0,endMs:3000,text:'Demo transcription. Configure Gemini/Whisper adapter for production STT.'}]}
