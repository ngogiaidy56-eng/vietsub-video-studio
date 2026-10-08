import winston from 'winston';
export const logger=winston.createLogger({level:process.env.LOG_LEVEL??'info',format:winston.format.combine(winston.format.timestamp(),winston.format.json()),transports:[new winston.transports.Console()]});
export const log={info:(message:string,meta?:unknown)=>logger.info(message,meta),error:(message:string,meta?:unknown)=>logger.error(message,meta),warn:(message:string,meta?:unknown)=>logger.warn(message,meta)};
