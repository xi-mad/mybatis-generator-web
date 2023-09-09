/** listData */
export const connectionListData = [
    {
        id: 1,
        name: "local",
        type: "MySQL_8",
        host: "127.0.0.1",
        port: "33068",
        username: "root",
        password: "123456",
        schema: "information_schema",
        encoding: "UTF-8",
    },
    {
        id: 3,
        name: "test",
        type: "MySQL_8",
        host: "127.0.0.1",
        port: "33068",
        username: "root",
        password: "123456",
        schema: "test",
        encoding: "UTF-8",
    },
];

const sleep = (delay) => new Promise((resolve) => setTimeout(resolve, delay));
export const getValue = async (name, delay = 1000) => {
    await sleep(delay);
    return {
        code: 0,
        message: "success",
        data: name,
    };
};
