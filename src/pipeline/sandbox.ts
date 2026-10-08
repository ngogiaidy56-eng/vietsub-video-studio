export async function dryRun(command:string){return {ok:true,command,mode:'dry-run',mutations:[] as string[]}}
