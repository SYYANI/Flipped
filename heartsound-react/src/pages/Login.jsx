import React, { useState, useEffect, useRef } from 'react';
import { Link, useNavigate } from 'react-router-dom'
import "../assets/less/login.less"
import { Button, Input, Form, Notification, Row } from "@douyinfe/semi-ui";
import { LoginApi, CaptchaApi, CheckCaptchaApi } from '../request/api'
import logoImg from '../assets/logo.png'

export default function Login() {
    const navigate = useNavigate();
    const formApiLogin = useRef();

    const [liked, setLiked] = useState(true);
    const [btnText] = useState("获取验证码");
    const [seconds, setSeconds] = useState(10);

    const getPhoneCode = () =>{
        let formApiTemp = formApiLogin.current
        let telephone = formApiTemp.getValue("telephone")
        console.log(telephone)
        CaptchaApi({
            telephone: telephone
        }).then(res=>{
            console.log(res)
            if(res.status === "success"){
                Notification.success({
                    content: "验证码发送成功",
                    duration: 2
                });
                let siv = setInterval(()=>{
                    setLiked(false);
                    setSeconds(v=>{
                        if(v===0){
                            setLiked(()=>{
                                return true
                            })
                            setSeconds(()=>{
                                return 10
                            })
                            clearInterval(siv)
                        }
                        return v-1
                    })
                }, 1000)
            }else{
                Notification.error({
                    content: res.message,
                    duration: 2
                });
            }
        })
    }
    //校验验证码
    const checkCaptcha = () =>{
        let formApiTemp = formApiLogin.current
        let telephone = formApiTemp.getValue("telephone")
        let captcha = formApiTemp.getValue("captcha")
        let values = {
            telephone: telephone,
            inputotp: captcha
        }
        console.log(values)
        CheckCaptchaApi({
            telephone: telephone,
            inputotp: captcha
        }).then(res=>{
            if(res.status === "success"){
                Notification.success({
                    content: "验证码校验成功",
                    duration: 2
                });
                localStorage.setItem('accessId',res.data.accessid)
                localStorage.setItem('telephone',res.data.telephone)
                localStorage.setItem('type',res.data.type)
                console.log(res)
                navigate("/")
            }else{
                Notification.error({
                    content: res.message,
                    duration: 2
                });
            }
        })
    }
    return (
        <div className='login'>
            <div className='login_box'>
                <div className='login_brand'>
                    
                    <img style={{ width: '80px', verticalAlign: 'middle', display: 'inline-block' }} src={logoImg} alt="" />
                    <div style={{ display: 'inline-block', marginLeft: '15px' }}>Flipped 心音</div>
                </div>

                <Form
                    getFormApi={formApi => formApiLogin.current = formApi}
                    labelPosition={'top'}
                    style={{ padding: '20px', width: '350px' }}
                >
                    {
                        ({ formState, values, formApi }) => {
                            return (
                                <Row>
                                    <Form.Input style={{ width: '350px' }} size="large" label="手机号码" field='telephone'
                                        rules={[
                                            { required: true, message: '必须填写' },
                                            { type: 'string', message: 'type error' },
                                            { pattern: /^1[3|4|5|7|8|9][0-9]\d{8}$/, message: '请输入正确的手机号码' }
                                        ]} />
                                    <div style={{ display: 'flex', alignItems: 'center', alignContent: 'space-around' }}>
                                        <Form.Input style={{ width: '240px' }} size="large" placeholder="验证码" noLabel={true} field='captcha'
                                            rules={[
                                                { required: true, message: '必须填写' },
                                                { type: 'string', message: 'type error' },
                                                { validator: (rule, value) => value > 0 && value < 10000, message: '4位验证码' }
                                            ]} />
                                        <Button size='large' onClick={getPhoneCode} disabled={!liked} style={{ marginLeft: '10px' }}>
                                            {
                                                liked
                                                    ?
                                                    <span>{btnText}</span>
                                                    :
                                                    <span>{seconds + ' s 后重新发送'}</span>
                                            }
                                        </Button>
                                    </div>
                                    <div style={{ display: 'inline' }}>
                                        <Button style={{ marginTop: 10, marginBottom: 5 }} onClick={checkCaptcha} size='large' type="primary" htmlType="submit" block>登录</Button>
                                    </div>
                                </Row>
                            )
                        }
                    }
                </Form>
            </div>
        </div>
    )
}
