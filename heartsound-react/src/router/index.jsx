import App from "../App";
import Login from "../pages/Login";
import {BrowserRouter as Router, Routes, Route} from 'react-router-dom'

const BaseRouter = () => (
    <Router>
       <Routes>
           <Route path='/' element = {<App/>}>
           </Route>
           <Route path='/login' element = {<Login/>}></Route>
       </Routes>
    </Router>
)

export default BaseRouter

