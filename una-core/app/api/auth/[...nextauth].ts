import NextAuth, { Account, AuthOptions } from "next-auth";
import BattleNetProvider from "next-auth/providers/battlenet";
import axios from "axios";

type WowProfileResponse = {
  wow_accounts: WoWAccountsResponse[];
};

type WoWAccountsResponse = {
  id: number;
  characters: WoWCharacterResponse[];
};

type WoWCharacterResponse = {
  id: number;
};

type WoWGuildRosterMemberResponse = {
  character: WoWCharacterResponse;
  rank: number;
};

type WoWGuildRosterResponse = {
  members: WoWGuildRosterMemberResponse[];
};

export const authOptions: AuthOptions = {
  providers: [
    BattleNetProvider({
      issuer: "https://eu.battle.net/oauth",
      authorization: { params: { scope: "openid wow.profile" } },
      clientId: process.env.APPLICATION_WOW_ID,
      clientSecret: process.env.APPLICATION_WOW_SECRET
    }),
    // maybe additional mail
  ],
  pages: {
    signIn: "/",
  },
  debug: process.env.NODE_ENV === "development",
  session: {
    strategy: "jwt",
  },
  //secret: process.env.AUTH_SECRET,
  secret: "say_lalisa_love_me_lalisa_love_me_hey",
  callbacks: {
    async signIn({ user, account, profile, email, credentials }) {
      const token = account?.access_token;
      console.log(`${token}`);

      if (token) {
        const result = await isProfileInGuild(token);

        if (result) {
          account.scope = `${result.rank}`;
          return true;
        }
      }

      return false;
    },
    async jwt({ token, account, profile }) {
        // Persist the OAuth access_token and or the user id to the token right after signin
        if (account) {
          token.accessToken = account.access_token;
          token.scope = account.scope;
          console.log(`Token scopes ${token.scope}`);
        }
        return token
      }
  },
};

async function isProfileInGuild(token: string) {
  try {
    const { data, status } = await axios.get<WowProfileResponse>(
      `https://eu.api.blizzard.com/profile/user/wow?namespace=profile-eu&locale=en_GB&access_token=${token}`,
      {
        headers: {
          Accept: "application/json",
        },
      }
    );

    const wowGuildResponse = await axios.get<WoWGuildRosterResponse>(
      `https://eu.api.blizzard.com/data/wow/guild/magtheridon/una-familia/roster?namespace=profile-eu&locale=en_GB&access_token=${token}`,
      {
        headers: {
          Accept: "application/json",
        },
      }
    );

    //All characters ID array
    const characterIds = data.wow_accounts
      .flatMap((account) => account.characters)
      .flatMap((child) => child.id);

    //Check if a character exists in the Guild
    const character = wowGuildResponse.data.members
      .filter((member) => characterIds.includes(member.character.id))
      .reduce((minRank, character) => {
        if (
          !minRank.rank ||
          (character.rank !== undefined && character.rank < minRank.rank)
        ) {
          return character;
        }

        return minRank;
      });

    return character;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      console.log("error message: ", error.message);
      return false;
    } else {
      console.log("unexpected error: ", error);
      return false;
    }
  }
}

export default NextAuth(authOptions);
