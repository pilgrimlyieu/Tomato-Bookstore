#import "simplepaper.typ": *

#show: project.with(title: "番茄商城系统用例文档")

#outline()

= 引言

== 目的

本文档描述了番茄商城系统的用户需求，为后期开发人员对系统的实现和验证工作提供指导。本文档面向开发人员、测试人员及最终用户，是了解系统功能的导航。

== 阅读说明

用例描述的约定为：
- *正常流程*：描述用户在正常情况下使用系统功能的步骤。
- *扩展流程*：描述在特定条件下，系统对用户操作的替代响应。
- *前置条件*：执行用例前必须满足的条件。
- *后置条件*：用例执行成功后，系统的状态。
- *优先级*：表示用例的重要性（高/中/低）。
- *特殊需求*：非功能性需求，如性能、安全等。

== 参考文献

- 《软件工程与计算（卷 2）——软件开发的技术基础》

= 用例图

#figure(
  image("images/use-case-diagram.svg", width: 25%),
  caption: [番茄商城系统用例图],
)

= 用例列表

// 用例计数器
#let use-case-counter = counter("use-case")
#use-case-counter.update(1)

// 角色枚举
#let Actor = (
  CUSTOMER: "顾客",
  ADMIN: "管理员",
)

// 优先级枚举
#let Priority = (
  HIGH: "高",
  MEDIUM: "中",
  LOW: "低",
)

// 用例集合
#let use-cases = (
  (
    name: "注册",
    actor: Actor.CUSTOMER,
    trigger: "用户访问注册页面",
    pre-cond: "无",
    post-cond: "用户账户创建成功，进入登录页面",
    priority: Priority.HIGH,
    normal-flow: [
      1. 用户输入用户名、密码、邮箱等信息。
      2. 系统验证输入信息的有效性（如格式、是否重复）。
      3. 系统创建用户账户。
      4. 系统跳转到登录页面。
    ],
    alt-flow: [
      - 2a. 如果用户名已存在，系统提示用户更换用户名。
      - 2b. 如果邮箱格式不正确，系统提示用户重新输入。
    ],
    special-req: "密码需要加密存储。",
  ),
  (
    name: "登录",
    actor: (Actor.CUSTOMER, Actor.ADMIN),
    trigger: "用户访问登录页面",
    pre-cond: "用户已注册（顾客）或已分配管理员账号",
    post-cond: "用户成功登录，进入系统首页（顾客）或管理后台（管理员）",
    priority: Priority.HIGH,
    normal-flow: [
      1. 用户输入用户名和密码。
      2. 系统验证用户名和密码的正确性。
      3. 系统根据用户角色，跳转到相应页面。
    ],
    alt-flow: [
      - 2a. 如果用户名或密码错误，系统提示用户重新输入。
      - 2b. 如果用户账户被锁定，系统提示用户联系管理员。
    ],
    special-req: "登录失败多次后，需要进行验证码验证或账户锁定。",
  ),
  (
    name: "查看个人信息",
    actor: Actor.CUSTOMER,
    trigger: "用户点击「个人信息」菜单",
    pre-cond: "用户已登录",
    post-cond: "系统显示用户的个人信息",
    priority: Priority.HIGH,
    normal-flow: [
      1. 系统获取当前登录用户的 ID。
      2. 系统根据用户 ID 查询数据库，获取用户信息。
      3. 系统显示用户的个人信息（用户名、邮箱、电话等）。
    ],
    special-req: "用户的敏感信息（如密码）不应显示。",
  ),
  (
    name: "修改个人信息",
    actor: Actor.CUSTOMER,
    trigger: "用户点击「修改个人信息」按钮",
    pre-cond: "用户已登录",
    post-cond: "用户的个人信息更新成功",
    priority: Priority.HIGH,
    normal-flow: [
      1. 系统显示用户的个人信息编辑页面。
      2. 用户修改个人信息（如邮箱、电话等）。
      3. 系统验证输入信息的有效性。
      4. 系统更新数据库中的用户信息。
      5. 系统提示用户修改成功。
    ],
    alt-flow: [
      - 3a. 如果邮箱格式不正确，系统提示用户重新输入。
    ],
    special-req: "用户不能修改用户名。",
  ),
  (
    name: "浏览所有商品",
    actor: Actor.CUSTOMER,
    trigger: "用户访问商城首页或点击「所有商品」菜单",
    pre-cond: "无",
    post-cond: "系统显示所有商品列表",
    priority: Priority.HIGH,
    normal-flow: [
      1. 系统查询数据库，获取所有商品信息。
      2. 系统分页显示商品列表（包含商品名称、作者、价格、图片等）。
    ],
    special-req: "支持分页显示，每页显示一定数量的商品。",
  ),
  (
    name: "查看指定商品",
    actor: Actor.CUSTOMER,
    trigger: "用户点击某个商品",
    pre-cond: "无",
    post-cond: "系统显示该商品的详细信息",
    priority: Priority.HIGH,
    normal-flow: [
      1. 用户点击商品列表中的某个商品。
      2. 系统根据商品 ID 查询数据库，获取商品详细信息。
      3. 系统显示商品详细信息（名称、作者、价格、图片、描述等）。
    ],
  ),
  (
    name: "搜索商品",
    actor: Actor.CUSTOMER,
    trigger: "用户在搜索框输入关键词并点击搜索按钮",
    pre-cond: "无",
    post-cond: "系统显示搜索结果列表",
    priority: Priority.HIGH,
    normal-flow: [
      1. 用户在搜索框输入关键词（书名、作者、ISBN 等）。
      2. 用户点击搜索按钮。
      3. 系统根据关键词查询数据库。
      4. 系统分页显示搜索结果列表。
    ],
    alt-flow: [
      - 3a. 如果没有找到匹配的商品，系统显示「未找到相关商品」提示。
      - 4a. 用户可以点击「下一页」或「上一页」浏览更多结果。
      - 4b. 用户可以选择排序方式（如按价格、出版日期等）。
    ],
    special-req: "支持模糊搜索。",
  ),
    (
    name: "添加到购物车",
    actor: Actor.CUSTOMER,
    trigger: "用户在商品详情页点击「加入购物车」按钮",
    pre-cond: "用户已登录",
    post-cond: "商品被添加到用户的购物车",
    priority: Priority.HIGH,
    normal-flow: [
      1. 用户在商品详情页点击「加入购物车」按钮。
      2. 系统将商品添加到当前用户的购物车。
      3. 系统提示用户添加成功。
    ],
    alt-flow: [
      - 2a. 如果商品库存不足，系统提示用户。
      - 2b. 如果用户未登录，系统提示用户先登录。
    ],
  ),
  (
    name: "查看购物车",
    actor: Actor.CUSTOMER,
    trigger: "用户点击「购物车」图标或链接",
    pre-cond: "用户已登录",
    post-cond: "系统显示购物车中的商品列表",
    priority: Priority.HIGH,
    normal-flow: [
      1. 系统获取当前用户的购物车信息。
      2. 系统显示购物车中的商品列表（商品名称、数量、单价、总价等）。
    ],
  ),
  (
    name: "修改购物车商品数量",
    actor: Actor.CUSTOMER,
    trigger: "用户在购物车页面修改商品数量",
    pre-cond: "用户已登录，购物车中有商品",
    post-cond: "购物车中商品数量更新，总价重新计算",
    priority: Priority.HIGH,
    normal-flow: [
      1. 用户在购物车页面修改商品数量。
      2. 系统验证输入数量的有效性（如必须为非负整数，不能超过库存）。
      3. 系统更新购物车中商品的数量。
      4. 系统重新计算订单总价。
    ],
    alt-flow: [
      - 2a. 如果输入数量无效，系统提示用户。
    ],
  ),
  (
    name: "从购物车删除商品",
    actor: Actor.CUSTOMER,
    trigger: "用户在购物车页面点击「删除」按钮",
    pre-cond: "用户已登录，购物车中有商品",
    post-cond: "商品从购物车中移除，总价重新计算",
    priority: Priority.HIGH,
    normal-flow: [
      1. 用户在购物车页面点击要删除商品的「删除」按钮。
      2. 系统从购物车中移除该商品。
      3. 系统重新计算订单总价。
    ],
  ),
  (
    name: "提交订单",
    actor: Actor.CUSTOMER,
    trigger: "用户点击「提交订单」按钮",
    pre-cond: "用户已登录，购物车中有商品",
    post-cond: "订单创建成功，进入支付页面",
    priority: Priority.HIGH,
    normal-flow: [
      1. 系统获取当前用户的购物车信息。
      2. 系统计算订单总价。
      3. 系统创建订单，并将订单信息存入数据库。
      4. 系统清空购物车。
      5. 系统跳转到支付页面。
    ],
    alt-flow: [
      - 2a. 如果商品库存不足，系统提示用户。
    ],
    special-req: "订单号需要唯一。",
  ),
(
    name: "修改订单状态（支付、取消）",
    actor: Actor.CUSTOMER,
    trigger: "用户点击「支付」或「取消」按钮",
    pre-cond: "用户已登录，订单已创建",
    post-cond: "订单状态更新成功",
    priority: Priority.HIGH,
    normal-flow: [
      1. 用户选择支付方式（如支付宝、微信支付）。
      2. 系统调用支付接口。
      3. 用户完成支付。
      4. 系统更新订单状态为「已支付」。
      5. （取消订单）用户点击「取消订单」按钮。
      6. 系统更新订单状态为「已取消」。
    ],
    alt-flow: [
      - 3a. 如果支付失败，系统提示用户重新支付。
      - 6a. 如果订单已发货，系统提示用户不能取消订单。
    ],
    special-req: "支付接口需要保证安全可靠。",
  ),
  (
    name: "查看广告",
    actor: Actor.CUSTOMER,
    trigger: "用户访问商城首页或特定页面",
    pre-cond: "无",
    post-cond: "系统显示广告信息",
    priority: Priority.MEDIUM,
    normal-flow: [
      1. 系统查询数据库，获取广告信息。
      2. 系统在页面指定位置显示广告（图片、链接等）。
    ],
  ),
(
    name: "发布书评",
    actor: Actor.CUSTOMER,
    trigger: "用户在书籍详情页点击「写书评」按钮",
    pre-cond: "用户已登录，且已购买该书籍（或系统允许未购买用户评论）",
    post-cond: "书评发布成功，显示在书籍详情页的书评列表中",
    priority: Priority.MEDIUM,
    normal-flow: [
      1. 系统显示书评编辑页面。
      2. 用户输入书评标题和内容。
      3. （可选）用户为书籍评分。
      4. 用户点击「发布」按钮。
      5. 系统将书评信息存入数据库，并关联到相应的书籍。
      6. 系统提示用户发布成功。
    ],
    alt-flow: [
      - 2a. 如果书评内容为空，系统提示用户输入内容。
      - 5a. 如果用户已发布过该书籍的书评，系统提示用户是否修改之前的书评。
    ],
    special-req: "书评内容需要进行敏感词过滤。",
  ),
(
    name: "撰写读书笔记",
    actor: Actor.CUSTOMER,
    trigger: "用户点击「撰写读书笔记」按钮",
    pre-cond: "用户已登录",
    post-cond: "读书笔记保存成功",
    priority: Priority.MEDIUM,
    normal-flow: [
      1. 系统显示读书笔记编辑页面。
      2. 用户输入读书笔记标题和内容。
      3. （可选）用户选择关联的书籍。
      4. 用户点击「保存」按钮。
      5. 系统将读书笔记信息存入数据库。
      6. 系统提示用户保存成功。
    ],
    alt-flow: [
      - 2a. 如果读书笔记内容为空，系统提示用户输入内容。
    ],
    special-req: "读书笔记内容需要进行敏感词过滤。",
  ),
  (
      name: "创建商品",
      actor: Actor.ADMIN,
      trigger: "管理员点击「创建商品」按钮",
      pre-cond: "管理员已登录",
      post-cond: "商品创建成功",
      priority: Priority.HIGH,
      normal-flow: [
        1. 系统显示商品创建页面。
        2. 管理员输入商品信息（名称、作者、价格、图片、描述、库存等）。
        3. 系统验证输入信息的有效性。
        4. 系统将商品信息存入数据库。
        5. 系统提示管理员创建成功。
      ],
      alt-flow: [
        - 3a. 如果商品名称已存在，系统提示管理员。
      ],
    ),
    (
      name: "更新商品信息",
      actor: Actor.ADMIN,
      trigger: "管理员点击「编辑商品」按钮",
      pre-cond: "管理员已登录",
      post-cond: "商品信息更新成功",
      priority: Priority.HIGH,
      normal-flow: [
        1. 系统显示商品编辑页面。
        2. 管理员修改商品信息。
        3. 系统验证输入信息的有效性。
        4. 系统更新数据库中的商品信息。
        5. 系统提示管理员修改成功。
      ],
      alt-flow: [
        - 3a. 如果商品名称已存在，系统提示管理员。
      ],
    ),
    (
      name: "删除商品",
      actor: Actor.ADMIN,
      trigger: "管理员点击「删除商品」按钮",
      pre-cond: "管理员已登录",
      post-cond: "商品删除成功",
      priority: Priority.HIGH,
      normal-flow: [
        1. 系统提示管理员确认是否删除商品。
        2. 管理员确认删除。
        3. 系统从数据库中删除商品信息。
        4. 系统提示管理员删除成功。
      ],
      alt-flow: [
        - 2a. 如果管理员取消删除，系统不执行任何操作。
      ],
    ),
    (
      name: "调整商品库存",
      actor: Actor.ADMIN,
      trigger: "管理员点击「调整库存」按钮",
      pre-cond: "管理员已登录",
      post-cond: "商品库存更新成功",
      priority: Priority.HIGH,
      normal-flow: [
        1. 系统显示商品库存调整页面。
        2. 管理员输入新的库存数量。
        3. 系统验证输入数量的有效性（如必须为非负整数）。
        4. 系统更新数据库中的商品库存数量。
        5. 系统提示管理员调整成功。
      ],
      alt-flow: [
        - 3a. 如果输入数量无效，系统提示管理员重新输入。
      ],
    ),
(
    name: "查看所有订单（管理员）",
    actor: Actor.ADMIN,
    trigger: "管理员进入订单管理页面",
    pre-cond: "管理员已登录",
    post-cond: "系统显示所有订单列表",
    priority: Priority.HIGH,
    normal-flow: [
      1. 系统查询数据库，获取所有订单信息。
      2. 系统分页显示订单列表（订单号、下单时间、用户、总价、状态等）。
    ],
    special-req: "支持分页显示，每页显示一定数量的订单。",
  ),
  (
    name: "按状态筛选订单（管理员）",
    actor: Actor.ADMIN,
    trigger: "管理员在订单管理页面选择订单状态",
    pre-cond: "管理员已登录",
    post-cond: "系统显示符合筛选条件的订单列表",
    priority: Priority.MEDIUM,
    normal-flow: [
      1. 管理员在订单管理页面选择订单状态（如待支付、已支付等）。
      2. 系统根据选择的状态查询数据库。
      3. 系统显示符合筛选条件的订单列表。
    ],
  ),
  (
    name: "修改订单状态（管理员）",
    actor: Actor.ADMIN,
    trigger: "管理员在订单管理页面点击「修改状态」按钮",
    pre-cond: "管理员已登录",
    post-cond: "订单状态更新成功",
    priority: Priority.HIGH,
    normal-flow: [
      1. 管理员在订单管理页面选择要修改的订单。
      2. 管理员选择新的订单状态（如发货）。
      3. 系统更新数据库中的订单状态。
      4. 系统提示管理员修改成功。
    ],
  ),
  (
    name: "管理书评",
    actor: Actor.ADMIN,
    trigger: "管理员进入书评管理页面",
    pre-cond: "管理员已登录",
    post-cond: "书评被删除或修改",
    priority: Priority.MEDIUM,
    normal-flow: [
      1. 系统显示所有书评列表。
      2. 管理员可以查看、删除或修改书评。
      3. 系统更新数据库中的书评信息。
    ],
  ),
  (
    name: "管理读书笔记",
    actor: Actor.ADMIN,
    trigger: "管理员进入读书笔记管理页面",
    pre-cond: "管理员已登录",
    post-cond: "读书笔记被删除或修改",
    priority: Priority.MEDIUM,
    normal-flow: [
      1. 系统显示所有读书笔记列表。
      2. 管理员可以查看、删除或修改读书笔记。
      3. 系统更新数据库中的读书笔记信息。
    ],
  ),
)

#let combine-actor(actor) = if type(actor) == array {
  actor.join("、")
} else {
  actor
}

#let use-case(case) = context [
  #set enum(indent: 0pt)
  #set list(indent: 0pt)

  #use-case-counter.step()
  #let id = use-case-counter.get().at(0)

  #heading(level: 2, outlined: false)[用例 #id：#case.name #label("user-case" + str(id))]
  #table(
    columns: (auto, 1fr),
    stroke: 0.5pt,
    align: horizon,
    inset: 5pt,
    [*ID*], str(id),
    [*名称*], case.name,
    [*参与者*], combine-actor(case.actor),
    [*触发条件*], case.trigger,
    [*前置条件*], case.pre-cond,
    [*后置条件*], case.post-cond,
    [*优先级*], case.priority,
    [*正常流程*], case.normal-flow,
    ..if "alt-flow" in case {
      ([*扩展流程*], case.alt-flow)
    },
    ..if "special-req" in case {
      ([*特殊需求*], case.special-req)
    },
  )
]

#table(
  columns: (auto, 1fr),
  [*参与者*], [*用例*],
  ..use-cases.enumerate().map(((i, case)) => {
    (
      combine-actor(case.actor),
      link(label("user-case" + str(i + 1)))[用例 #(i + 1)：#case.name],
    )
  }).flatten()
)

= 详细用例描述

#for case in use-cases {
  use-case(case)
}