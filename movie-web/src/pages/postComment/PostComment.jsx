import React, { useState } from 'react'
import logo2 from '../../assets/png2.png'
import './postComment.scss'
import { useContext, useEffect } from 'react'
import { AuthContext } from '../authContext/AuthContext'
import axios from 'axios'
const PostComment = props => {
    const [info, setInfo] = useState();
    useEffect(() => {
        const getInfo = async () => {
            try {
                const res = await axios.get(
                    `${process.env.REACT_APP_GATEWAY_URL}/api/v1/identity/users/myInfo`, {
                        headers: {
                            Authorization: "Bearer "+JSON.parse(localStorage.getItem("user")).token
                          }
                    }
                )
                
                setInfo(res.data.result);
            } catch (err) {
                console.log(err)
            }
        }
        getInfo();
    }, [props]);
    const [comment, setComment] = useState('');
    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const data = await axios.post(
                `${process.env.REACT_APP_GATEWAY_URL}/api/v1/movie/flims/interactive/post/` + props.id,
                {
                    comment : comment,
                },
                {
                    headers: {
                        Authorization: "Bearer "+JSON.parse(localStorage.getItem("user")).token
                      }
                }
            )
            window.location.reload();
        } catch (err) {
            console.log(err);
        }
    }
    return (
        <div className='body'>
            <div className='comment-session'>
                <div className='comment-box'>
                    <div className='user'>
                        <div className='image'><img src={logo2} alt='image' /></div>
                        <div className='name'>{info?.username}</div>
                    </div>
                    <form action="" method='post'>
                        <textarea name='comment'
                            id='comment'
                            placeholder='Your Message'
                            onChange={(e) => setComment(e.target.value)}
                        ></textarea>
                        <button className='comment-submit' onClick={handleSubmit}>Comment</button>
                    </form>
                </div>
            </div>
        </div>
    )
}

export default PostComment
