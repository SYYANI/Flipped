import './assets/base.less'
import { useNavigate } from 'react-router-dom'
import heartsoundIcon from './assets/logo.png'
import React, { useState, useEffect, useRef } from 'react';
import { Typography, TextArea, Toast, Layout, Nav, Button, Modal, Avatar, Dropdown, Card, Row, Col, Space, Notification, Form, Popover, Tag, Empty, Table } from '@douyinfe/semi-ui';
import {AddDrugApi, UpdateRecordApi, AddRecordApi, GetDoctorInfoApi, GetHospitalListApi, UpdateDoctorInfoApi, GetPatientListApi, GetAllPatientListApi, GetDiseaseListApi, GetAudioListApi, GetRecordListApi, JudgeApi, GetDrugListApi } from './request/api'
import { IconBox, IconSetting, IconPlay, IconPause } from '@douyinfe/semi-icons';
import { IllustrationNoResult, IllustrationNoResultDark } from '@douyinfe/semi-illustrations';
import moment from 'moment'
import ReactAudioPlayer from 'react-audio-player';
export default function App() {
  const { Header, Footer, Content } = Layout;
  const navigate = useNavigate()
  const { Meta } = Card;
  const { Text } = Typography;
  const [dataSource, setData] = useState();
  //播放器句柄
  const audioRef = useRef(null)
  //切换播放按钮
  const [audioType, setAudioType] = useState(true)
  //录音诊断输入框
  const audioTxtRef = useRef();
  //病历详情输入框
  const recordTxtRef = useRef();
  //病历详情输入框
  const recordKnowTxtRef = useRef();
  //病历详情输入框
  const recordUpdateTxtRef = useRef();
  //医生详细信息输入框
  const txtRef = useRef(null)
  //录音诊断句柄
  const formApiAudio = useRef();
  //更新病历句柄
  const formApiRecordInfoUpdate = useRef();
  //新增病历句柄
  const formApiAddKnow = useRef();
  //新增药品句柄
  const formApiAddDrug = useRef();
  //新增患者表单句柄
  const formApiAdd = useRef();
  //更新表单句柄
  const formApiUpdate = useRef();
  //患者病历详情信息
  const [visibleRecordInfo, setVisibleRecordInfo] = useState()
  //患者详情信息
  const [visiblePatientDetail, setVisiblePatientDetail] = useState(false);
  //新增患者信息信息
  const [visibleAdd, setVisibleAdd] = useState(false);
  //新增患者病历
  const [visibleAddKnow, setVisibleAddKnow] = useState(false);
  //单条音频诊断
  const [visibleAudio, setVisibleAudio] = useState(false);
  //新增药品
  const [visibleAddDrug, setVisibleAddDrug] = useState(false);
  //更新个人信息
  const [visibleUpdate, setVisibleUpdate] = useState(false)
  //医院列表
  const [hospitalList, sethospitalList] = useState()
  //医生信息更新预加载
  const [initDoctorCardValues, setInitDoctorCardValues] = useState()
  //病历信息预加载
  const [initRecordCardValues, setInitRecordCardValues] = useState()
  //疾病列表
  const [diseaseList, setDiseaseList] = useState()
  //药品列表
  const [drugList, setDrugList] = useState()
  //所有患者列表信息
  const [allPatientList, setAllPatientList] = useState()
  //当前患者记录详情
  const [patientNowReceived, setPatientNowReceived] = useState()
  //当前录音
  const audioExample = {
    audioUrl: "",
    date: "",
    aiResult: "",
    docResult: ""
  }
  //当前录音
  const [audioNow, setAudioNow] = useState(audioExample)
  //当前病历
  const [recordNow, setRecordNow] = useState()
  //当前患者录音列表
  const [patientNowAudioList, setpatientNowAudioList] = useState()
  //当前患者病历列表
  const [patientNowRecordList, setpatientNowRecordList] = useState()
  //患者列表信息
  const [patientReceivedList, setPatientReceivedList] = useState()
  //患者列表table的列
  const columns_patient_receive = [
    {
      title: '序号',
      align: 'center',
      dataIndex: 'patientId',
      render: (text, record) => <>
        <Tag style={{ color: 'black' }}>{text}</Tag>
      </>
    },
    {
      title: '姓名',
      dataIndex: 'patientName',
      align: 'center',
      width: 'calc(300px - 76px)'
    },
    {
      title: '联系方式',
      align: 'center',
      dataIndex: 'relationTel',
      render: (text, record) => <>
        {
          text
        }
      </>
    }
  ]
  //药品列表列
  const columns_medicine_item = [
    {
      title: '药名',
      dataIndex: 'medicineName',
      align: 'center',
      width: 'calc(300px - 76px)'
    },
    {
      title: '使用方法',
      align: 'center',
      dataIndex: 'instruction',
      render: (text, record) => <>
        {
          text
        }
      </>
    }
  ]
  //患者录音列表table的列
  const columns_patient_audio = [
    {
      title: '序号',
      align: 'center',
      dataIndex: 'fileId',
      render: (text, record) => <>
        <Tag style={{ color: 'black' }}>{text}</Tag>
      </>
    },
    {
      title: '诊断日期',
      align: 'center',
      dataIndex: 'date',
      render: (text, record) => <>
        {
          <Text>
            {moment(text).format("YYYY-MM-DD hh:mm")}
          </Text>
        }
      </>
    },
    {
      title: 'AI诊断',
      align: 'center',
      dataIndex: 'aiResult',
      render: (text, record) => <>
        {
          text
        }
      </>
    }
  ]
  //患者录音病历table的列
  const columns_patient_record = [
    {
      title: '病历号',
      align: 'center',
      dataIndex: 'recordId',
      render: (text, record) => <>
        <Tag style={{ color: 'black' }}>{text}</Tag>
      </>
    },
    {
      title: '疾病名称',
      align: 'center',
      dataIndex: 'diseaseName',
      render: (text, record) => <>
        {
          text
        }
      </>
    },
    {
      title: '更新日期',
      align: 'center',
      dataIndex: 'updateDate',
      render: (text, record) => <>
        {
          <Text>
            {moment(text).format("YYYY-MM-DD hh:mm")}
          </Text>
        }
      </>
    }
  ]
  const empty = (
    <Empty
      image={<IllustrationNoResult />}
      darkModeImage={<IllustrationNoResultDark />}
      description={'搜索无结果'}
    />
  );
  // 一般加个空数组就是为了模仿componentDidMounted
  useEffect(() => {
    let usertype = localStorage.getItem('type')
    console.log(usertype)
    switch (usertype) {
      case '1':
        navigate('/')
        break
      case '-1':
        Notification.error({
          content: "请完善个人信息！",
          duration: 2
        });
        break
      default:
        navigate('/login')
        localStorage.clear();   // 清除localStorage中的数据 
        break
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  useEffect(() => {
    refreshData();
  }, [])

  const refreshData = () => {
    GetHospitalListApi({
    }).then(res => {
      // console.log(res)
      if (res.status === "success") {
        const hospitaltemp = [...res.data]
        sethospitalList(hospitaltemp)
      } else {
        Notification.error({
          content: res.data.errMsg,
          duration: 2
        });
      }
    })
    GetDoctorInfoApi({
    }).then(res => {
      // console.log(res)
      if (res.status === "success") {
        setData(res.data)
      } else {
        Notification.error({
          content: res.data.errMsg,
          duration: 2
        });
      }
    })
    GetPatientListApi({
    }).then(res => {
      // console.log(res)
      if (res.status === "success") {
        setPatientReceivedList(res.data)
      } else {
        Notification.error({
          content: res.data.errMsg,
          duration: 2
        });
      }
    })
    GetAllPatientListApi({
    }).then(res => {
      // console.log(res)
      if (res.status === "success") {
        setAllPatientList(res.data)
      } else {
        Notification.error({
          content: res.data.errMsg,
          duration: 2
        });
      }
    })
    GetDiseaseListApi({
    }).then(res => {
      // console.log(res)
      if (res.status === "success") {
        setDiseaseList(res.data)
      } else {
        Notification.error({
          content: res.data.errMsg,
          duration: 2
        });
      }
    })
  }
  //准备病历
  const showRecordUpdateInfo = (record) => {
    setRecordNow(record)
    let newRecord = deepCopy(record)
    let ini = {
      diseaseId: newRecord.diseaseId,
      recordDetail: newRecord.detail
    }
    if (recordUpdateTxtRef.current != null) {
      recordUpdateTxtRef.current.setValue(newRecord.detail)
    }
    GetDrugListApi({
      recordid: record.recordId
    }).then(res => {
      if (res.status === "success") {
        setDrugList(res.data)
      } else {
        Notification.error({
          content: res.data.errMsg,
          duration: 2
        });
      }
    })
    setInitRecordCardValues(ini)
    setVisibleRecordInfo(true)
  }
  //准备单条音频详细信息
  const showDetailAudio = (record) => {
    setVisibleAudio(true)
    console.log(record)
    setAudioNow(record)
  }
  //进入病人详情页面准备数据
  const showDetailInfoReceive = (record) => {
    setVisiblePatientDetail(true)
    console.log(record)
    setPatientNowReceived(record)
    getPatientNowEverything(record.patientId)
  }
  const getPatientNowEverything = (patientId) => {
    console.log("patientId: " + patientId)
    GetAudioListApi({
      patientid: patientId
    }).then(res => {
      console.log(res)
      if (res.status === "success") {
        setpatientNowAudioList(res.data)
      } else {
        Notification.error({
          content: res.data.errMsg,
          duration: 2
        });
      }
    })
    GetRecordListApi({
      patientid: patientId
    }).then(res => {
      console.log(res)
      if (res.status === "success") {
        setpatientNowRecordList(res.data)
      } else {
        Notification.error({
          content: res.data.errMsg,
          duration: 2
        });
      }
    })
  }
  // 退出登录
  const logout = () => {
    Toast.info('退出成功，即将返回登录页')
    localStorage.clear();   // 清除localStorage中的数据 
    setTimeout(() => navigate('/login'), 1500)
  }
  //深拷贝
  function deepCopy(data) {
    if (typeof data !== 'object' || data === null) {
      throw new TypeError('传入参数不是对象')
    }
    let newData = {};
    const dataKeys = Object.keys(data);
    dataKeys.forEach(value => {
      const currentDataValue = data[value];
      // 基本数据类型的值和函数直接赋值拷贝 
      if (typeof currentDataValue !== "object" || currentDataValue === null) {
        newData[value] = currentDataValue;
      } else if (Array.isArray(currentDataValue)) {
        // 实现数组的深拷贝
        newData[value] = [...currentDataValue];
      } else if (currentDataValue instanceof Set) {
        // 实现set数据的深拷贝
        newData[value] = new Set([...currentDataValue]);
      } else if (currentDataValue instanceof Map) {
        // 实现map数据的深拷贝
        newData[value] = new Map([...currentDataValue]);
      } else {
        // 普通对象则递归赋值
        newData[value] = deepCopy(currentDataValue);
      }
    });
    return newData;
  }

  const reDataAudio = (key) => {
    if (key) {
      return "inline"
    } else {
      return "none"
    }
  }
  const reData = (key) => {
    switch (key) {
      case 'title':
        if (dataSource != null) {
          return dataSource.name
        }
        break
      default:
        return ('');
    }
  }
  const reDataPatient = (key) => {
    switch (key) {
      case 'title':
        if (patientNowReceived != null) {
          return patientNowReceived.patientName
        }
        break
      default:
        return ('');
    }
  }
  //病人卡片详情
  const cardDetailPatient = () => {
    if (patientNowReceived != null) {
      // eslint-disable-next-line 
      return (
        <div>
          <div >
            联系人: {patientNowReceived.relationName}
          </div>
          <div style={{ marginTop: "6px" }}>
            联系人号码: {patientNowReceived.relationTel}
          </div>
        </div>
      )
    }
  }
  //医生卡片详情
  const cardDetail = () => {
    if (dataSource != null) {
      // eslint-disable-next-line 
      return (
        <div>
          <div >
            医院: {dataSource.hospitalName}
          </div>
          <div style={{ marginTop: "6px" }}>
            办公室位置: {dataSource.officeLocation}
          </div>
          <div style={{ marginTop: "6px" }}>
            办公室号码: {dataSource.officeTel}
          </div>
          <div style={{ marginTop: "6px" }}>
            详情: {dataSource.title}
          </div>
        </div>
      )
    }
  }
  const updateInfo = () => {
    console.log(dataSource)
    if (dataSource != null) {
      let newDoctorDetail = deepCopy(dataSource)
      let ini = {
        doctorName: newDoctorDetail.name,
        doctorDetail: newDoctorDetail.title,
        doctorOffice: newDoctorDetail.officeLocation,
        officeTel: newDoctorDetail.officeTel,
      }
      setInitDoctorCardValues(ini)
    }
    setVisibleUpdate(true)
  }
  const addPatientModal = () => {
    setVisibleAdd(true)
  }
  const onOkCheckDetail = () => {
    setVisiblePatientDetail(false)
  }

  const onOkUpdate = () => {
    console.log(formApiUpdate.current.getFormState().values)
    const doctorInfo = formApiUpdate.current.getFormState().values
    if (doctorInfo.hospitalId == null
      || doctorInfo.doctorName == null
      || doctorInfo.doctorOffice == null
      || doctorInfo.officeTel == null
      || doctorInfo.doctorDetail == null) {
      Notification.error({
        title: '个人信息填写不规范',
        content: "操作失败，请检查填写内容！",
        duration: 2
      })
    } else {
      UpdateDoctorInfoApi({
        doctorname: doctorInfo.doctorName,
        hospitalid: doctorInfo.hospitalId,
        officelocation: doctorInfo.doctorOffice,
        officetel: doctorInfo.officeTel,
        title: doctorInfo.doctorDetail
      }).then(res => {
        if (res.status === "success") {
          Notification.success({
            title: '个人信息更新成功',
            content: "操作成功！",
            duration: 2
          })
          refreshData()
          localStorage.setItem('type', '1')
        } else {
          Notification.error({
            content: res.data.errMsg,
            duration: 2
          })
        }
      })
    }
    setVisibleUpdate(false)
  }
  const onOkRecordInfoUpdate = () => {
    setVisibleRecordInfo(false)
    console.log(formApiRecordInfoUpdate.current.getFormState().values)
    console.log("patientId: " + patientNowReceived.patientId)
    console.log("recordId: " + recordNow.recordId)
    const recordInfo = formApiRecordInfoUpdate.current.getFormState().values
    if (recordInfo.diseaseId == null ||
      recordInfo.recordDetail == null ||
      recordNow.recordId == null) {
      Notification.error({
        title: '病历信息填写不规范',
        content: "操作失败，请检查填写内容！",
        duration: 2
      })
    } else {
      UpdateRecordApi({
        patientid: patientNowReceived.patientId,
        diseaseid: recordInfo.diseaseId,
        detail: recordInfo.recordDetail,
        recordid: recordNow.recordId
      }).then(res => {
        if (res.status === "success") {
          Notification.success({
            title: '病历更新成功',
            content: "操作成功！",
            duration: 2
          })
          refreshData()
          getPatientNowEverything(patientNowReceived.patientId)
        } else {
          Notification.error({
            content: res.data.errMsg,
            duration: 2
          })
        }
      })
    }
  }
  const onOkAdd = () => {
    setVisibleAdd(false)
    console.log(formApiAdd.current.getFormState().values)
    const recordInfo = formApiAdd.current.getFormState().values
    if (recordInfo.patientId == null ||
      recordInfo.diseaseId == null ||
      recordInfo.recordDetail == null) {
      Notification.error({
        title: '病历信息填写不规范',
        content: "操作失败，请检查填写内容！",
        duration: 2
      })
    } else {
      AddRecordApi({
        patientid: recordInfo.patientId,
        diseaseid: recordInfo.diseaseId,
        detail: recordInfo.recordDetail
      }).then(res => {
        if (res.status === "success") {
          Notification.success({
            title: '病历添加成功',
            content: "操作成功！",
            duration: 2
          })
          refreshData()
        } else {
          Notification.error({
            content: res.data.errMsg,
            duration: 2
          })
        }
      })
    }
  }
  const onAddItem = () =>{
    console.log(formApiAddDrug.current.getFormState().values)
    setVisibleAddDrug(false)
    const drugInfo = formApiAddDrug.current.getFormState().values
    AddDrugApi({
      recordid: recordNow.recordId,
      medicinename: drugInfo.medicineName,
      instruction: drugInfo.instruction
    }).then(res=>{
      if(res.status === "success"){
        Notification.success({
          title: '药品添加成功',
          content: "操作成功！",
          duration: 2
        })
        refreshData()
        GetDrugListApi({
          recordid:  recordNow.recordId
        }).then(res => {
          if (res.status === "success") {
            setDrugList(res.data)
          } else {
            Notification.error({
              content: res.data.errMsg,
              duration: 2
            });
          }
        })
      }else{
        Notification.error({
          content: res.data.errMsg,
          duration: 2
        })
      }
    })
  } 
  const onOkAddKnow = () => {
    setVisibleAddKnow(false)
    console.log(formApiAddKnow.current.getFormState().values)
    console.log("patientId: " + patientNowReceived.patientId)
    const recordInfo = formApiAddKnow.current.getFormState().values
    if (recordInfo.diseaseId == null ||
      recordInfo.recordDetail == null) {
      Notification.error({
        title: '病历信息填写不规范',
        content: "操作失败，请检查填写内容！",
        duration: 2
      })
    } else {
      AddRecordApi({
        patientid: patientNowReceived.patientId,
        diseaseid: recordInfo.diseaseId,
        detail: recordInfo.recordDetail
      }).then(res => {
        if (res.status === "success") {
          Notification.success({
            title: '病历添加成功',
            content: "操作成功！",
            duration: 2
          })
          refreshData()
          getPatientNowEverything(patientNowReceived.patientId)
        } else {
          Notification.error({
            content: res.data.errMsg,
            duration: 2
          })
        }
      })
    }
  }
  //单次诊断完毕
  const onOkAudio = () => {
    setVisibleAudio(false)
    console.log(formApiAudio.current.getFormState().values)
    console.log(audioNow.fileId)
    const audioInfo = formApiAudio.current.getFormState().values
    if (audioInfo.docResult === null ||
      audioNow.fileId === null) {
      Notification.error({
        title: '诊断结果填写不规范',
        content: "操作失败，请检查填写内容！",
        duration: 2
      })
    } else {
      JudgeApi({
        fileid: audioNow.fileId,
        docresult: audioInfo.docResult
      }).then(res => {
        if (res.status === "success") {
          Notification.success({
            title: '诊断结果更新成功',
            content: "操作成功！",
            duration: 2
          })
          refreshData()
          getPatientNowEverything(audioNow.patientId)
        } else {
          Notification.error({
            content: res.data.errMsg,
            duration: 2
          })
        }
      })
    }
  }
  const onClose = () => {
    setVisibleUpdate(false)
    setVisibleAdd(false)
    setVisiblePatientDetail(false)
  }
  //疾病列表
  const diseaseListItem = () => {
    // console.log(diseaseList)
    return (diseaseList.map((v, idx) => (
      <Form.Select.Option key={v.diseaseId} value={v.diseaseId}>{v.diseaseName}</Form.Select.Option>
    )))
  }
  //所有患者列表
  const selectAllPatientListItem = () => {
    // console.log(allPatientList)
    return (allPatientList.map((v, idx) => (
      <Form.Select.Option key={v.patientId} value={v.patientId}>{v.patientName}</Form.Select.Option>
    )))
  }
  //医院列表
  const selectItem = () => {
    // console.log(hospitalList)
    return (hospitalList.map((v, idx) => (
      <Form.Select.Option key={v.hospitalId} value={v.hospitalId}>{v.hospitalName}</Form.Select.Option>
    )))
  }
  //等价写法
  // const onChange=(value)=>{
  //   console.log(value)
  // }
  return (
    <Layout className='fullScreen' style={{ border: '10px solid var(--semi-color-border)' }}>
      <Layout>
        <Header style={{ backgroundColor: 'var(--semi-color-bg-1)' }}>
          <Nav
            header={{
              logo: <img src={heartsoundIcon} alt="" />,
              text: 'Flipeed 心音',
            }}
            mode="horizontal"
          >
            <Nav.Footer>
              <Dropdown
                position="bottomRight"
                trigger={'hover'}
                render={
                  <Dropdown.Menu>
                    <Dropdown.Item icon={<IconBox />}>修改个人资料</Dropdown.Item>
                    <Dropdown.Item onClick={logout} icon={<IconSetting />}>退出登录</Dropdown.Item>
                  </Dropdown.Menu>
                }
              >
                <Avatar color="orange" size="small">
                  GL
                </Avatar>
              </Dropdown>
            </Nav.Footer>
          </Nav>
        </Header>
        <Content
          style={{
            padding: '24px',
            backgroundColor: 'var(--semi-color-bg-0)',
          }}
        >
          <Modal
            title="更新病历"
            visible={visibleRecordInfo}
            onOk={onOkRecordInfoUpdate}
            onCancel={() => {
              setVisibleRecordInfo(false)
            }}
            style={{ width: 600 }}
          >
            <Form
              getFormApi={formApi => formApiRecordInfoUpdate.current = formApi}
              labelPosition={'top'}
              initValues={initRecordCardValues}
              style={{ padding: '20px', width: '500px' }}>
              {
                ({ formState, values, formApi }) => {
                  return (
                    <Row>
                      <Form.Select filter onChange={value => {
                        console.log("diseaseId: " + value)
                      }} field='diseaseId' label="疾病列表" style={{ width: 500 }}>
                        {diseaseListItem()}
                      </Form.Select>
                      <Form.Input
                        size="large"
                        label="详细诊断"
                        field='recordDetail'
                        style={{ display: "none" }}
                        trigger='blur'
                      />
                      <TextArea ref={recordUpdateTxtRef} defaultValue={formApiRecordInfoUpdate.current.getFormState().values.recordDetail} onBlur={() => {
                        formApiRecordInfoUpdate.current.setValue('recordDetail', recordUpdateTxtRef.current.value)
                        console.log(recordUpdateTxtRef.current.value)
                      }} autosize maxCount={100} />
                    </Row>
                  )
                }
              }
            </Form>
            <Card
              style={{ marginTop: 20 }}
              title='药品列表'
              footerLine={true}
              footerStyle={{ height: '50px', display: 'flex', justifyContent: 'flex-end' }}
              footer={
                <Space>
                  <Button onClick={() => {
                    // setVisibleAddItem(true)
                    setVisibleAddDrug(true)
                  }} type='primary'>新增</Button>
                </Space>
              }
            >
              <Table
                onRow={(record, index) => {
                  return {
                    onClick: event => {
                      console.log(record)
                    }, // 点击行
                  };
                }}
                style={{ minHeight: 340 }}
                columns={columns_medicine_item}
                rowKey="medicineId"
                dataSource={drugList}
                empty={empty}
                pagination={false}
                hideExpandedColumn={false}
              />
            </Card>
            <Modal
              title="添加药品"
              visible={visibleAddDrug}
              onOk={onAddItem}
              onCancel={() => {
                setVisibleAddDrug(false)
              }}>
              <Form
                getFormApi={formApi => formApiAddDrug.current = formApi}
                labelPosition={'top'}
                style={{ padding: '10px', width: 240 }}
              >
                {
                  ({ formState, values, formApi }) => {
                    return (
                      <div>
                        <Form.Input size="large" label="药品名称" field='medicineName'
                          rules={[
                            { required: true, message: '必须填写' },
                            { type: 'string', message: 'type error' }
                          ]} />
                        <Form.Input size="large" label="使用方法" field='instruction'
                          rules={[
                            { required: true, message: '必须填写' },
                            { type: 'string', message: 'type error' }
                          ]} />
                      </div>
                    )
                  }
                }
              </Form>
            </Modal>
          </Modal>
          <Modal
            title="添加病历"
            visible={visibleAddKnow}
            onOk={onOkAddKnow}
            onCancel={() => {
              setVisibleAddKnow(false)
            }}
            style={{ width: 400 }}>
            <Form
              getFormApi={formApi => formApiAddKnow.current = formApi}
              labelPosition={'top'}
              style={{ padding: '20px', width: '300px' }}>
              {
                ({ formState, values, formApi }) => {
                  return (
                    <Row>
                      <Form.Select filter onChange={value => {
                        console.log("diseaseId: " + value)
                      }} field='diseaseId' label="疾病列表" style={{ width: 300 }}>
                        {diseaseListItem()}
                      </Form.Select>
                      <Form.Input
                        size="large"
                        label="详细诊断"
                        field='recordDetail'
                        style={{ display: "none" }}
                        trigger='blur'
                      />
                      <TextArea ref={recordKnowTxtRef} onBlur={() => {
                        formApiAddKnow.current.setValue('recordDetail', recordKnowTxtRef.current.value)
                        console.log(recordKnowTxtRef.current.value)
                      }} autosize maxCount={100} />

                    </Row>
                  )
                }
              }
            </Form>
          </Modal>
          <Modal
            title="诊断详情"
            visible={visibleAudio}
            onOk={onOkAudio}
            onCancel={() => {
              setVisibleAudio(false)
            }}
            style={{ width: 400 }}>
            <Form
              getFormApi={formApi => formApiAudio.current = formApi}
              labelPosition={'top'}
              style={{ padding: '20px', width: '300px' }}>
              <Row>
                <ReactAudioPlayer
                  src={audioNow.audioUrl}
                  autoPlay={false}
                  ref={audioRef}
                  onPlay={() => {
                    setAudioType(false)
                  }}
                  onPause={() => {
                    setAudioType(true)
                  }}
                ></ReactAudioPlayer>
                <IconPlay size='extra-large' style={{ display: reDataAudio(audioType) }} onClick={() => {
                  audioRef.current.audioEl.current.play()
                }}></IconPlay>
                <IconPause size='extra-large' style={{ display: reDataAudio(!audioType) }} onClick={() => {
                  audioRef.current.audioEl.current.pause()
                }}></IconPause>
              </Row>
              <Row style={{ marginTop: '10px' }}>
                <Text heading={'5'}>
                  诊断日期: {moment(audioNow.date).format("YYYY-MM-DD hh:mm")}
                </Text>
              </Row>
              <Row style={{ marginTop: '10px' }}>
                <Text heading={'5'}>
                  Ai诊断结果: {audioNow.aiResult}
                </Text>
              </Row>

              <Form.Input
                size="large"
                field='docResult'
                label="医生诊断"
                style={{ display: "none" }}
                trigger='blur'
              />
              <TextArea ref={audioTxtRef} defaultValue={audioNow.docResult} onBlur={() => {
                formApiAudio.current.setValue('docResult', audioTxtRef.current.value)
                // console.log(audioTxtRef.current.value)
              }} autosize maxCount={100} />
            </Form>
          </Modal>
          <Modal
            title="更新个人信息"
            visible={visibleUpdate}
            onOk={onOkUpdate}
            onCancel={onClose}
            style={{ width: 400 }}
          >
            <Form
              getFormApi={formApi => formApiUpdate.current = formApi}
              initValues={initDoctorCardValues}
              labelPosition={'top'}
              style={{ padding: '20px', width: '300px' }}
            >
              {
                ({ formState, values, formApi }) => {
                  return (
                    <Row>
                      <Form.Input size="large" label="姓名" field='doctorName'
                        rules={[
                          { required: true, message: '必须填写' },
                          { type: 'string', message: 'type error' }
                        ]} />
                      <Form.Select onChange={value => {
                        console.log("patientId: " + value)
                      }} field="hospitalId" label="医院" style={{ width: '300px' }}>
                        {selectItem()}
                      </Form.Select>
                      <Form.Input size="large" label="办公室位置" field='doctorOffice'
                        rules={[
                          { required: true, message: '必须填写' },
                          { type: 'string', message: 'type error' }
                        ]} />
                      <Form.Input size="large" label="办公室电话" field='officeTel'
                        rules={[
                          { required: true, message: '必须填写' },
                          { type: 'string', message: 'type error' }
                        ]} />
                      {/* <Form.Input size="large" label="个人简介" field='doctorDetail'
                        rules={[
                          { required: true, message: '必须填写' },
                          { type: 'string', message: 'type error' }
                        ]} /> */}
                      <Form.Input
                        size="large"
                        field='doctorDetail'
                        label="个人简介"
                        style={{ display: "none" }}
                        trigger='blur'
                      />
                      <TextArea ref={txtRef} defaultValue={() => {
                        if (dataSource != null) {
                          return dataSource.title
                        } else {
                          return ""
                        }
                      }} onBlur={() => {
                        formApiUpdate.current.setValue('doctorDetail', txtRef.current.value)
                        console.log(txtRef.current.value)
                      }} autosize maxCount={100} />
                    </Row>
                  )
                }
              }
            </Form>
          </Modal>
          <Modal
            title="添加患者记录"
            visible={visibleAdd}
            onOk={onOkAdd}
            onCancel={onClose}
            style={{ width: 400 }}>
            <Form
              getFormApi={formApi => formApiAdd.current = formApi}
              labelPosition={'top'}
              style={{ padding: '20px', width: '300px' }}
            >
              {
                ({ formState, values, formApi }) => {
                  return (
                    <Row>
                      <Form.Select filter onChange={value => {
                        console.log("patientId: " + value)
                      }} field='patientId' label="患者列表" style={{ width: 300 }}>
                        {selectAllPatientListItem()}
                      </Form.Select>
                      <Form.Select filter onChange={value => {
                        console.log("diseaseId: " + value)
                      }} field='diseaseId' label="疾病列表" style={{ width: 300 }}>
                        {diseaseListItem()}
                      </Form.Select>

                      <Form.Input
                        size="large"
                        label="详细诊断"
                        field='recordDetail'
                        style={{ display: "none" }}
                        trigger='blur'
                      />
                      <TextArea ref={recordTxtRef} onBlur={() => {
                        formApiAdd.current.setValue('recordDetail', recordTxtRef.current.value)
                        console.log(recordTxtRef.current.value)
                      }} autosize maxCount={100} />

                    </Row>
                  )
                }
              }
            </Form>
          </Modal>
          <Modal
            title="患者详情"
            fullScreen
            visible={visiblePatientDetail}
            onOk={onOkCheckDetail}
            onCancel={onClose}>
            <Row>
              <Col span={10}>
                <Card
                  title={
                    <Meta
                      title={reDataPatient('title')}
                    />
                  }
                >
                  {cardDetailPatient()}
                </Card>
                <Card
                  style={{ marginTop: 20 }}
                  title="录音列表"
                >
                  <Table
                    onRow={(record, index) => {
                      return {
                        onClick: event => {
                          showDetailAudio(record)
                          // showDetailInfoReceive(record)
                        }, // 点击行
                      };
                    }}
                    style={{ minHeight: 140 }}
                    columns={columns_patient_audio}
                    rowKey="recordId"
                    dataSource={patientNowAudioList}
                    empty={empty}
                    pagination={false}
                    hideExpandedColumn={false}
                  ></Table>
                </Card>
              </Col>
              <Col span={14}>
                <Card
                  style={{ marginLeft: 20 }}
                  title="病历列表"
                  footerLine={true}
                  footerStyle={{ height: '30px', display: 'flex', justifyContent: 'flex-end' }}
                  footer={
                    <Button onClick={() => {
                      setVisibleAddKnow(true)
                    }} type='primary'>新增</Button>
                  }
                >
                  <Table
                    onRow={(record, index) => {
                      return {
                        onClick: event => {
                          console.log(record)
                          showRecordUpdateInfo(record)
                          // showDetailInfoReceive(record)
                        }, // 点击行
                      };
                    }}
                    style={{ minHeight: 140 }}
                    columns={columns_patient_record}
                    rowKey="recordId"
                    dataSource={patientNowRecordList}
                    empty={empty}
                    pagination={false}
                    hideExpandedColumn={false}
                  ></Table>
                </Card>
              </Col>
            </Row>
          </Modal>
          <div className='veticalContainer'
            style={{
              borderRadius: '10px',
              border: '1px solid var(--semi-color-border)',
              // height: '100%'
              padding: '32px',
            }}
          >
            <Row gutter={[16, 16]}>
              <Col span={8}>
                <Card
                  style={{ maxWidth: 340 }}
                  title={
                    <Meta
                      title={reData('title')}
                    />
                  }
                  footerLine={true}
                  footerStyle={{ height: '30px', display: 'flex', justifyContent: 'flex-end' }}
                  footer={
                    <Button onClick={updateInfo} type='primary'>更新</Button>
                  }
                >
                  {cardDetail()}
                </Card>
              </Col>
              <Col span={16}>
                <Popover showArrow position='left' content={
                  <article style={{ padding: 12 }} >
                    点击查看病人详情
                  </article>
                }
                >
                  <Card
                    title="病人列表"
                    footerLine={true}
                    footerStyle={{ height: '30px', display: 'flex', justifyContent: 'flex-end' }}
                    footer={
                      <Button onClick={addPatientModal} type='primary'>新增</Button>
                    }
                  >
                    <Table
                      onRow={(record, index) => {
                        return {
                          onClick: event => {
                            console.log(record)
                            showDetailInfoReceive(record)
                          }, // 点击行
                        };
                      }}
                      style={{ minHeight: 140 }}
                      columns={columns_patient_receive}
                      rowKey="recordId"
                      dataSource={patientReceivedList}
                      empty={empty}
                      pagination={false}
                      hideExpandedColumn={false}
                    ></Table>
                  </Card>
                </Popover>
              </Col>
            </Row>
          </div>
        </Content>
        <Footer
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            padding: '20px',
            color: 'var(--semi-color-text-2)',
            backgroundColor: 'rgba(var(--semi-grey-0), 1)',
          }}
        >
          <span
            style={{
              display: 'flex',
              alignItems: 'center',
            }}
          >
            <span> </span>
          </span>
          <span>
            <span style={{ marginRight: '24px' }}>平台客服</span>
            <span>反馈建议</span>
          </span>
        </Footer>
      </Layout>
    </Layout >
  );
};