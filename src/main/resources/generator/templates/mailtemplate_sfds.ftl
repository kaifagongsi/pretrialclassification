<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="description" content="email code">
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
<div style="background-color:#ECECEC; padding: 35px;">
    <h2>各位领导、同事，以下是今天新分配的保护中心案件列表! 请注意查收！</h2>
    <table  width="90%" class="table">
        <thead>
        <tr>
            <th>预审编号</th>
            <th>部门</th>
            <th>主分人</th>
            <th>发明名称</th>
            <th>粗分号</th>
            <th>分配时间</th>
        </tr>
        </thead>
        <tbody>
        <#list model as item>
            <tr>
                <td >
                    ${item.id}
                </td>
                <td >
                    ${item.dep1} ${item.dep2}
                </td>
                <td >
                    ${item.worker}
                </td>
                <td >
                    ${item.mingcheng}
                </td>
                <td >
                    ${item.simpleclasscode}
                </td>
                <td >
                    ${item.fenpeitime}
                </td>
            </tr>
        </#list>
        </tbody>
    </table>
</div>
</body>
<style type="text/css">
    table
    {
        border-collapse: collapse;
        margin: 0 auto;
        text-align: center;
    }
    table td, table th
    {
        border: 1px solid #cad9ea;
        color: #666;
        height: 30px;
    }
    table thead th
    {
        background-color: #CCE8EB;
        width: 100px;
    }
    table tr:nth-child(odd)
    {
        background: #fff;
    }
    table tr:nth-child(even)
    {
        background: #F5FAFA;
    }
</style>
</html>

