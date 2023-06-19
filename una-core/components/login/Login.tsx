'use client';

import { FaBattleNet } from 'react-icons/fa';
import { signIn } from 'next-auth/react';

const Login = () => {
    return (
        <button
            className="
                flex
                flex-row
                items-center
                justify-between
                gap-3
                bg-blue-700
                hover:bg-blue-800
                text-white font-semibold
                md:py-4
                md:px-8
                rounded-full
                focus:outline-none
                focus:shadow-outline
                text-xl
                "
            onClick={() => signIn('battlenet')}    
        >
            <FaBattleNet size="64" >

            </FaBattleNet>
            <div className='hidden md:block'>
                Login using Battle.net!
            </div>
        </button>
    );
}

export default Login;