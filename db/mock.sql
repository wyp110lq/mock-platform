  --Mock配置表--
   CREATE TABLE T_MOCK_CONFIG
   (
      ID NUMBER(20) NOT NULL,
      SERVICE_NAME VARCHAR2(100) NOT NULL,
      SERVICE_TYPE VARCHAR2(20) NOT NULL,
      SERVICE_URI VARCHAR2(200) NOT NULL,
      SERVICE_STATUS NUMBER(1) NOT NULL,
      REQ_METHOD VARCHAR2(20),
      REQ_CONTENT_TYPE VARCHAR2(50),
      RES_CONTENT_TYPE VARCHAR2(50) NOT NULL,
      RES_STATUS NUMBER(3) NOT NULL,
      RES_RESULT CLOB,
      SERVICE_TIME NUMBER(3) NOT NULL,
      SERVICE_NUMBER NUMBER(10),
      CLASS_NAME VARCHAR2(200),
      WSDL_CONTENT CLOB,
      INTERFACE_CODE CLOB,
      CREATE_TIME DATE NOT NULL,
      UPDATE_TIME DATE NOT NULL
   );

   COMMENT ON COLUMN T_MOCK_CONFIG.ID IS '主键id';
   COMMENT ON COLUMN T_MOCK_CONFIG.SERVICE_NAME IS '服务名称';
   COMMENT ON COLUMN T_MOCK_CONFIG.SERVICE_TYPE IS '服务类型';
   COMMENT ON COLUMN T_MOCK_CONFIG.SERVICE_URI IS '服务URI';
   COMMENT ON COLUMN T_MOCK_CONFIG.SERVICE_STATUS IS '服务状态:0未发布,1已发布';
   COMMENT ON COLUMN T_MOCK_CONFIG.REQ_METHOD IS '请求方式';
   COMMENT ON COLUMN T_MOCK_CONFIG.REQ_CONTENT_TYPE IS '参数类型';
   COMMENT ON COLUMN T_MOCK_CONFIG.RES_CONTENT_TYPE IS '返回类型';
   COMMENT ON COLUMN T_MOCK_CONFIG.RES_STATUS IS '返回状态码';
   COMMENT ON COLUMN T_MOCK_CONFIG.RES_RESULT IS '返回结果';
   COMMENT ON COLUMN T_MOCK_CONFIG.SERVICE_TIME IS '服务耗时(秒)';
   COMMENT ON COLUMN T_MOCK_CONFIG.SERVICE_NUMBER IS '服务编号';
   COMMENT ON COLUMN T_MOCK_CONFIG.CLASS_NAME IS '类名';
   COMMENT ON COLUMN T_MOCK_CONFIG.WSDL_CONTENT IS 'WSDL内容';
   COMMENT ON COLUMN T_MOCK_CONFIG.INTERFACE_CODE IS '接口代码';
   COMMENT ON COLUMN T_MOCK_CONFIG.CREATE_TIME IS '创建时间';
   COMMENT ON COLUMN T_MOCK_CONFIG.UPDATE_TIME IS '更新时间';

   ALTER TABLE T_MOCK_CONFIG ADD CONSTRAINT PK_T_MOCK_CONFIG PRIMARY KEY (ID);
   COMMENT ON TABLE T_MOCK_CONFIG IS 'Mock配置表';

