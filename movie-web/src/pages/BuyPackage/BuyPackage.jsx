import React, { useEffect, useState } from 'react'
import Header from '../../components/header/Header'
import axios from 'axios'
import './buyPackage.scss'
import PackageItem from '../../components/packageItem/PackageItem'
import Footer from '../../components/footer/Footer'
const BuyPackage = () => {

  const [listPackage, setListPackage] = useState([]);
  useEffect(() => {
    const getPackage = async() => {
      const res = await axios.get(`${process.env.REACT_APP_GATEWAY_URL}/api/v1/bills/getAll` ,{
        headers: {
            Authorization: "Bearer "+JSON.parse(localStorage.getItem("user")).token
          }
    })
      
      setListPackage(res.data.result);
    }
    getPackage();
  },[])

  return (
    <>
      <Header/>
      <div className='gallery'>
        
        {
          listPackage.map((item) => 
            
            <PackageItem id={item.id}/>
          )
        }
      </div>
      <Footer/>
    </>
  )
}

export default BuyPackage
