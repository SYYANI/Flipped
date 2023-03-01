import React from 'react';
import Router from './router';
import { createRoot } from 'react-dom/client';

const container = document.getElementById('root');
const root = createRoot(container); 

root.render(<Router/>);