import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

import Notifications from "components/Notifications/Notifications";
import {getUserById} from "api/users"

import pinway_logo from "images/pinway_logo.png";
import placeholder from  "images/place_holder.png";
import { useStore } from "./StoreContext";



const HomeMenu = () => {
  const navigate = useNavigate();
  const {search: globalSearch, setSearch: setGlobalSearch} = useStore();
  const [search, setSearch] = useState(globalSearch || '');
  const [user, setUser] = useState(null);

  const handleAddPost = async () => {
    navigate('/posts/create');
  }

  const handleSearchChange = (e) => {
    setSearch(e.target.value); 
  };

  const handleSearchClick = async () => {
    navigate('/home');
    console.log("Search bar value: ", search);
    setGlobalSearch(search);
    
  };


  const handleLogOut = async () => {
    localStorage.setItem("Bearer", null)
    localStorage.setItem("UserId",0)
    navigate("/login")
  }

  const handleProfile = async () => {
    navigate('/users/profile');
  }

  useEffect(() => {
    if (search !== null && search !== undefined) {
        console.log('Global search value', search);
    }
    const fetch = async () => {
        try {
          const response = await getUserById(localStorage.getItem("UserId"));
          setUser(response);
        } catch (e) {
          console.log(e);
        }
      };

      fetch();
  }, [search]);

  return (
    <div>
      <nav style={{backgroundColor: '#d7a8f5', padding: '5px'}} className="navbar navbar-expand-lg">
        <div className="container-fluid">
          <a className="navbar-brand" href="/home">
            <img
              src={pinway_logo}
              alt=""
              width="132"
              height="32"
              className="d-inline-block align-text-top"></img>
          </a>
          <div className="collapse navbar-collapse" id="navbarNavAltMarkup">
            <div className="navbar-nav">
              <button style={{paddingRight: '10px'}} type="button" className="btn btn-outline-secondary pr-3" onClick ={handleAddPost}>New</button>
            </div>
            <div className="input-group" style={{ marginLeft: '10px', width: '80%' }}>
              <input type="text" className="form-control" value={search} onChange={handleSearchChange} placeholder="Search" />
              <button className="btn btn-outline-secondary" type="button" onClick={handleSearchClick}>Search</button>
            </div>
          </div>
          <div className="d-flex">
            <Notifications/>
            <div className="dropdown col-3">
              <img
                className="border-0 d-flex d-flex align-items-center justify-content-center dropdown-toggle rounded-circle"
                // not sure if this is ok
                // src={"http://localhost:8083/user-photos/" + user.id + "/" + user.image_path}
                src = {placeholder}
                alt=''
                width="35"
                height="35"
                aria-expanded="false"
                data-bs-toggle="dropdown"
              ></img>
              <div className="dropdown-menu dropdown-menu-end" aria-labelledby="dropdownMenuButton">
                <button
                  className="dropdown-item"
                  style={{ fontSize: '13px', padding: '5px 10px' }} type="button"
                  onClick={handleProfile}
                >
                  Profile
                </button>
                <button
                  className="dropdown-item"
                  style={{ fontSize: '13px', padding: '5px 10px' }} type="button"
                  onClick={handleLogOut}
                >
                  Log out
                </button>
              </div>

            </div>
          </div>
        </div>
      </nav>
    </div>
  );
};

export default HomeMenu;