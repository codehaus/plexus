CREATE TABLE ProcessInstance (
  instanceId            int primary key,
  processId             varchar(100),
  createdTime           bigint,
  endTime               bigint,
  errorNessage          varchar(8000),
  completed             int
);

CREATE TABLE StepInstance (
  executorId            varchar(100),
  startTime             long,
  endTime               long,
  exceptionStackTrace   varchar(8000)
);
