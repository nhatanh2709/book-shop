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
      const res = await axios.get(`${process.env.REACT_APP_GATEWAY_URL}/service3/api/bills/getAll` , {
          headers: {
            token: "Bearer " + JSON.parse(localStorage.getItem("user")).accessToken
          }
        }
      )
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
            <PackageItem id={item._id}/>
          )
        }
      </div>
      <Footer/>
    </>
  )
}

export default BuyPackage
