import React, { useEffect, useState } from 'react'
import { useContext } from 'react';
import { AuthContext } from '../../pages/authContext/AuthContext';
import './packageItem.scss'
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
const PackageItem = props => {
    const { user } = useContext(AuthContext);
    const [Package, setPackage] = useState([]);
    const [infoTransfer, setInfoTransfer] = useState('');
    const [url ,setUrl] = useState('');
    const navigate = useNavigate();
    useEffect(() => {
        const getPackage = async () => {
            const res = await axios.get(`${process.env.REACT_APP_GATEWAY_URL}/api/v1/bills/get/${props.id}`,{
                headers: {
                    Authorization: "Bearer "+JSON.parse(localStorage.getItem("user")).token
                  }
            });
            console.log(res.data.result);
            setPackage(res.data.result);
        }
        if (props.id !== undefined) {
            getPackage();
        }
    }, [props])

    useEffect(() => {
        const getInfoTransfer = async() => {
            const res = await axios.post(`${process.env.REACT_APP_GATEWAY_URL}/api/v1/bills/transactions`, {
                    packageId: Package.id
                }, {
                    headers: {
                        Authorization: "Bearer "+JSON.parse(localStorage.getItem("user")).token
                      }
                })
            
            setInfoTransfer(res.data.result);
            setUrl(res.data.result.payUrl);
            console.log(url);
        }
        if(Package !== undefined && Package.id !== undefined) {
            getInfoTransfer();
        }
    },[Package])
    return (
        <div class="product-content">
            <h3>{Package.name}</h3>
            <p>{Package.desc}</p>
            <h6>{Package.price}</h6>
            <button class="buy" onClick={() => window.location.href = url}>Buy</button>
        </div>
    )
}

export default PackageItem
