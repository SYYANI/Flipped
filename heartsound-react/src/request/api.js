import request from './request'

// 注册
export const RegisterApi = (params) => request.post('/customer/update', params)

//获取短信验证码
export const CaptchaApi = (params) => request.post('/user/getotp', params)

//校验验证码
export const CheckCaptchaApi = (params) => request.post('/user/checkotp', params)

//医生获取个人信息
export const GetDoctorInfoApi = (params) => request.post('/doctor/detaild', params)

//获取医院列表
export const GetHospitalListApi = (params) => request.post('/hospital/list', params)

//医生更新个人信息
export const UpdateDoctorInfoApi = (params) => request.post('/doctor/update', params)

//获取医生关联患者列表
export const GetPatientListApi = (params) => request.post('/patient/listbydoctor', params)

//获取所有患者列表
export const GetAllPatientListApi = (params) => request.post('/patient/listd', params)

//获取疾病列表
export const GetDiseaseListApi = (params) => request.post('/disease/list', params)

//添加病历记录
export const AddRecordApi = (params) => request.post('/record/add', params)

//更新病历记录
export const UpdateRecordApi = (params) => request.post('/record/update', params)

//获取录音列表
export const GetAudioListApi = (params) => request.post('/file/list', params)

//获取患者病历列表
export const GetRecordListApi = (params) => request.post('/record/getdetailbypatient', params)

//医生做出判断
export const JudgeApi = (params) => request.post('/file/docjudge', params)

//获取药品列表
export const GetDrugListApi = (params) => request.post('/medicine/listbyrecord', params)

//添加药品
export const AddDrugApi = (params) => request.post('/medicine/add', params)

