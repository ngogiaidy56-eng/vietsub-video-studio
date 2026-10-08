export async function handleCallback(update,env){return {method:'answerCallbackQuery',callback_query_id:update.callback_query.id,text:'Đã nhận'}};
