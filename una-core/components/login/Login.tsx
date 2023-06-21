"use client";

import { FaBattleNet } from "react-icons/fa";
import { signIn } from "next-auth/react";
import { useState } from "react";

const Login = () => {
  const [isLogging, setIsLogging] = useState(false);

  return (
    <button
      className={`                  
                flex
                flex-row
                items-center
                justify-between
                gap-3
                bg-blue-700
                ${isLogging ? `bg-blue-800` : `hover:bg-blue-800`}
                text-white font-semibold
                md:py-4
                md:px-8
                px-4
                py-2
                rounded-full
                focus:outline-none
                focus:shadow-outline
                text-xl`}
      onClick={() => {
        setIsLogging(true);
        signIn("battlenet");
      }}
      disabled={isLogging}
    >
      <FaBattleNet className="w-16 h-16"></FaBattleNet>
      {!isLogging && (
        <div className="hidden md:block">Login using Battle.net!</div>
      )}

      {isLogging && (
        <div className="flex flex-row items-center justify-between animate-[pulse_1.25s_linear_infinite] space-x-6 sm:space-x-3">
          {[...Array(3)].map((_) => (
            <div
              className="
                        bg-white
                        rounded-full
                        h-4
                        w-4 
                        sm:h-2
                        sm:w-2
                "
            ></div>
          ))}
        </div>
      )}
    </button>
  );
};

export default Login;
