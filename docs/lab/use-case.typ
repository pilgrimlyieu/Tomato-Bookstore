#import "../template/simplepaper.typ": *

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
  image("images/use-cases-diagram.svg", height: 100%),
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
      1. 用户输入用户名、密码、确认密码、邮箱、手机号等信息。
      2. 系统验证输入信息的有效性（如格式、是否重复）。
      3. 系统对密码进行加密处理。
      4. 系统创建用户账户。
      5. 系统跳转到登录页面。
    ],
    alt-flow: [
      - 2a. 如果用户名已存在，系统提示用户更换用户名。
      - 2b. 如果邮箱格式不正确，系统提示用户重新输入。
      - 2c. 如果两次输入的密码不一致，系统提示用户重新输入。
      - 2d. 如果手机号格式不正确，系统提示用户重新输入。
    ],
    special-req: "密码需要加密存储，用户名具有唯一性。",
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
      3. 系统生成 JWT Token 并返回给客户端。
      4. 系统根据用户角色，跳转到相应页面。
    ],
    alt-flow: [
      - 2a. 如果用户名或密码错误，系统提示登录失败。
    ],
    special-req: "使用 JWT Token 进行身份验证。",
  ),
  (
    name: "查看个人信息",
    actor: Actor.CUSTOMER,
    trigger: "用户点击「个人中心」菜单",
    pre-cond: "用户已登录",
    post-cond: "系统显示用户的个人信息、书评和读书笔记概况",
    priority: Priority.HIGH,
    normal-flow: [
      1. 系统获取当前登录用户的 ID。
      2. 系统根据用户 ID 查询数据库，获取用户信息。
      3. 系统获取用户的书评和读书笔记数量及预览。
      4. 系统显示用户的个人信息页面，包含多个标签页。
    ],
    special-req: "用户的敏感信息（如密码）不应显示。",
  ),
  (
    name: "修改个人信息",
    actor: Actor.CUSTOMER,
    trigger: "用户在个人信息页面点击「保存修改」按钮",
    pre-cond: "用户已登录",
    post-cond: "用户的个人信息更新成功",
    priority: Priority.HIGH,
    normal-flow: [
      1. 系统显示用户的个人信息编辑表单。
      2. 用户修改个人信息（如邮箱、电话、地址等）。
      3. 系统验证输入信息的有效性。
      4. 系统更新数据库中的用户信息。
      5. 系统提示用户修改成功。
    ],
    alt-flow: [
      - 3a. 如果邮箱格式不正确，系统提示用户重新输入。
      - 3b. 如果手机号格式不正确，系统提示用户重新输入。
    ],
    special-req: "用户不能修改用户名。",
  ),
  (
    name: "修改密码",
    actor: Actor.CUSTOMER,
    trigger: "用户在安全设置页面修改密码",
    pre-cond: "用户已登录",
    post-cond: "用户密码更新成功",
    priority: Priority.HIGH,
    normal-flow: [
      1. 用户输入新密码和确认密码。
      2. 系统验证两次输入的密码是否一致。
      3. 系统对新密码进行加密处理。
      4. 系统更新数据库中的用户密码。
      5. 系统清空表单并提示用户修改成功。
    ],
    alt-flow: [
      - 2a. 如果两次输入的密码不一致，系统提示用户重新输入。
    ],
    special-req: "密码需要加密存储。",
  ),
  (
    name: "上传头像",
    actor: Actor.CUSTOMER,
    trigger: "用户点击「更换头像」按钮",
    pre-cond: "用户已登录",
    post-cond: "用户头像更新成功",
    priority: Priority.MEDIUM,
    normal-flow: [
      1. 系统显示头像上传对话框。
      2. 用户选择图片文件。
      3. 系统验证文件格式和大小（JPG、PNG、GIF、WebP，最大 5MB）。
      4. 系统上传文件到服务器。
      5. 系统更新用户的头像 URL。
      6. 系统提示用户上传成功。
    ],
    alt-flow: [
      - 3a. 如果文件格式不支持，系统提示用户选择正确格式。
      - 3b. 如果文件过大，系统提示用户选择较小的文件。
    ],
    special-req: "支持多种图片格式，文件大小限制。",
  ),
  (
    name: "浏览所有商品",
    actor: Actor.CUSTOMER,
    trigger: "用户访问商城首页或点击「全部商品」菜单",
    pre-cond: "无",
    post-cond: "系统显示所有商品列表",
    priority: Priority.HIGH,
    normal-flow: [
      1. 系统查询数据库，获取所有商品信息。
      2. 系统分页显示商品列表（包含商品名称、价格、评分、封面图等）。
      3. 用户可以选择不同的排序方式（默认、价格升序/降序、评分降序）。
    ],
  ),
  (
    name: "搜索商品",
    actor: Actor.CUSTOMER,
    trigger: "用户在搜索框输入关键词",
    pre-cond: "无",
    post-cond: "系统显示搜索结果列表",
    priority: Priority.HIGH,
    normal-flow: [
      1. 用户在搜索框输入关键词（书名、描述等）。
      2. 系统根据关键词实时查询数据库。
      3. 系统分页显示搜索结果列表。
      4. 用户可以选择排序方式。
    ],
    alt-flow: [
      - 2a. 如果没有找到匹配的商品，系统显示空状态提示。
    ],
  ),
  (
    name: "查看商品详情",
    actor: Actor.CUSTOMER,
    trigger: "用户点击某个商品",
    pre-cond: "无",
    post-cond: "系统显示该商品的详细信息",
    priority: Priority.HIGH,
    normal-flow: [
      1. 用户点击商品列表中的某个商品。
      2. 系统根据商品 ID 查询数据库，获取商品详细信息。
      3. 系统同时获取商品库存信息。
      4. 系统获取该商品的书评和读书笔记。
      5. 系统显示商品详细信息页面，包含商品信息、书评、读书笔记等。
    ],
    special-req: "商品详情页面应包含完整的商品信息和用户互动内容。",
  ),
  (
    name: "添加到购物车",
    actor: Actor.CUSTOMER,
    trigger: "用户在商品详情页或商品卡片点击「加入购物车」按钮",
    pre-cond: "用户已登录",
    post-cond: "商品被添加到用户的购物车",
    priority: Priority.HIGH,
    normal-flow: [
      1. 用户选择商品数量（默认为 1）。
      2. 用户点击「加入购物车」按钮。
      3. 系统验证商品库存是否充足。
      4. 系统将商品添加到当前用户的购物车。
      5. 系统提示用户添加成功。
    ],
    alt-flow: [
      - 3a. 如果商品库存不足，系统提示库存不足。
      - 1a. 如果用户未登录，系统提示用户先登录。
    ],
    special-req: "需要实时检查库存状态。",
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
      2. 系统显示购物车中的商品列表（商品名称、数量、单价、小计等）。
      3. 系统计算并显示总计金额。
    ],
    alt-flow: [
      - 1a. 如果购物车为空，系统显示空状态页面。
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
      1. 用户使用数量选择器修改商品数量。
      2. 系统验证输入数量的有效性（必须为正整数，不能超过库存）。
      3. 系统更新购物车中商品的数量。
      4. 系统重新计算订单总价。
    ],
    alt-flow: [
      - 2a. 如果输入数量超过库存，系统提示库存不足。
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
      1. 用户点击要删除商品的「删除」按钮。
      2. 系统显示确认删除对话框。
      3. 用户确认删除。
      4. 系统从购物车中移除该商品。
      5. 系统重新计算订单总价。
    ],
    alt-flow: [
      - 3a. 如果用户取消删除，系统不执行任何操作。
    ],
  ),
  (
    name: "清空购物车",
    actor: Actor.CUSTOMER,
    trigger: "用户点击「清空购物车」按钮",
    pre-cond: "用户已登录，购物车中有商品",
    post-cond: "购物车中所有商品被清空",
    priority: Priority.MEDIUM,
    normal-flow: [
      1. 用户点击「清空购物车」按钮。
      2. 系统显示确认清空对话框。
      3. 用户确认清空。
      4. 系统清空购物车中的所有商品。
      5. 系统显示购物车空状态页面。
    ],
    alt-flow: [
      - 3a. 如果用户取消清空，系统不执行任何操作。
    ],
  ),
  (
    name: "订单结算",
    actor: Actor.CUSTOMER,
    trigger: "用户在购物车页面点击「去结算」按钮",
    pre-cond: "用户已登录，购物车中有商品",
    post-cond: "进入结算页面",
    priority: Priority.HIGH,
    normal-flow: [
      1. 系统获取购物车中的商品信息。
      2. 系统显示结算页面，包含收货地址、支付方式、订单商品等。
      3. 用户填写或确认收货地址。
      4. 用户选择支付方式（目前仅支持支付宝）。
      5. 系统显示订单汇总信息。
    ],
    special-req: "需要验证所有必填信息的完整性。",
  ),
  (
    name: "提交订单",
    actor: Actor.CUSTOMER,
    trigger: "用户在结算页面点击「提交订单」按钮",
    pre-cond: "用户已登录，已填写完整的订单信息",
    post-cond: "订单创建成功，进入支付页面",
    priority: Priority.HIGH,
    normal-flow: [
      1. 系统验证订单信息的完整性。
      2. 系统检查商品库存是否充足。
      3. 系统创建订单记录。
      4. 系统更新商品库存（冻结相应数量）。
      5. 系统清空购物车。
      6. 系统跳转到支付页面。
    ],
    alt-flow: [
      - 2a. 如果商品库存不足，系统提示库存不足并返回购物车。
      - 1a. 如果订单信息不完整，系统提示用户补充信息。
    ],
    special-req: "订单号需要唯一，库存操作需要原子性。",
  ),
  (
    name: "支付订单",
    actor: Actor.CUSTOMER,
    trigger: "用户在支付页面点击「支付宝支付」按钮",
    pre-cond: "用户已登录，订单已创建且状态为待支付",
    post-cond: "订单状态更新为已支付或支付失败",
    priority: Priority.HIGH,
    normal-flow: [
      1. 系统调用支付接口生成支付表单。
      2. 系统显示支付表单或跳转到支付页面。
      3. 用户在支付平台完成支付。
      4. 支付平台返回支付结果。
      5. 系统更新订单状态为「已支付」。
      6. 系统跳转到支付成功页面。
    ],
    alt-flow: [
      - 4a. 如果支付失败，系统跳转到支付失败页面。
      - 2a. 如果支付表单生成失败，系统提示用户稍后重试。
    ],
    special-req: "支付接口需要保证安全可靠，订单超时自动取消。",
  ),
  (
    name: "查看订单列表",
    actor: Actor.CUSTOMER,
    trigger: "用户点击「我的订单」菜单",
    pre-cond: "用户已登录",
    post-cond: "系统显示用户的订单列表",
    priority: Priority.HIGH,
    normal-flow: [
      1. 系统获取当前用户的所有订单。
      2. 系统分页显示订单列表。
      3. 用户可以按订单状态筛选订单。
      4. 用户可以搜索特定订单号。
    ],
    special-req: "支持状态筛选。",
  ),
  (
    name: "查看订单详情",
    actor: Actor.CUSTOMER,
    trigger: "用户点击订单列表中的「订单详情」按钮",
    pre-cond: "用户已登录",
    post-cond: "系统显示订单的详细信息",
    priority: Priority.HIGH,
    normal-flow: [
      1. 系统根据订单 ID 查询订单详细信息。
      2. 系统显示订单详情页面，包含商品信息、收货地址、支付信息等。
      3. 根据订单状态显示相应的操作按钮（支付、取消等）。
    ],
  ),
  (
    name: "取消订单",
    actor: Actor.CUSTOMER,
    trigger: "用户点击「取消订单」按钮",
    pre-cond: "用户已登录，订单状态为待支付",
    post-cond: "订单状态更新为已取消",
    priority: Priority.HIGH,
    normal-flow: [
      1. 系统显示取消订单确认对话框。
      2. 用户确认取消。
      3. 系统更新订单状态为「已取消」。
      4. 系统释放冻结的商品库存。
      5. 系统提示用户取消成功。
    ],
    alt-flow: [
      - 2a. 如果用户取消操作，系统不执行任何操作。
      - 1a. 如果订单状态不允许取消，系统提示用户。
    ],
    special-req: "只有待支付状态的订单可以取消。",
  ),
  (
    name: "查看广告",
    actor: Actor.CUSTOMER,
    trigger: "用户访问商城首页",
    pre-cond: "无",
    post-cond: "系统显示广告信息",
    priority: Priority.MEDIUM,
    normal-flow: [
      1. 系统查询数据库，获取所有有效的广告信息。
      2. 系统在首页广告位置显示广告卡片。
      3. 用户可以点击广告跳转到关联的商品详情页。
    ],
    special-req: "广告应该与商品正确关联。",
  ),
  (
    name: "发布书评",
    actor: Actor.CUSTOMER,
    trigger: "用户在书籍详情页点击「撰写书评」按钮",
    pre-cond: "用户已登录",
    post-cond: "书评发布成功，显示在书籍详情页的书评列表中",
    priority: Priority.MEDIUM,
    normal-flow: [
      1. 系统显示书评编辑表单。
      2. 用户输入评分（0-10 分）和评论内容（可选）。
      3. 用户点击「发布评价」按钮。
      4. 系统验证输入信息的有效性。
      5. 系统将书评信息存入数据库，并关联到相应的书籍和用户。
      6. 系统提示用户发布成功。
      7. 系统更新书评列表显示。
    ],
    alt-flow: [
      - 4a. 如果用户已发布过该书籍的书评，系统提示用户编辑现有书评。
    ],
    special-req: "每个用户对每本书只能发布一条书评。",
  ),
  (
    name: "编辑书评",
    actor: Actor.CUSTOMER,
    trigger: "用户点击自己书评的「编辑」按钮",
    pre-cond: "用户已登录，已发布过书评",
    post-cond: "书评更新成功",
    priority: Priority.MEDIUM,
    normal-flow: [
      1. 系统显示书评编辑对话框，预填充现有内容。
      2. 用户修改评分或评论内容。
      3. 用户点击「更新评价」按钮。
      4. 系统验证输入信息的有效性。
      5. 系统更新数据库中的书评信息。
      6. 系统提示用户更新成功。
      7. 系统更新书评列表显示。
    ],
  ),
  (
    name: "删除书评",
    actor: Actor.CUSTOMER,
    trigger: "用户点击自己书评的「删除」按钮",
    pre-cond: "用户已登录，已发布过书评",
    post-cond: "书评删除成功",
    priority: Priority.MEDIUM,
    normal-flow: [
      1. 系统显示删除确认对话框。
      2. 用户确认删除。
      3. 系统从数据库中删除书评信息。
      4. 系统提示用户删除成功。
      5. 系统更新书评列表显示。
    ],
    alt-flow: [
      - 2a. 如果用户取消删除，系统不执行任何操作。
    ],
  ),
  (
    name: "查看用户书评列表",
    actor: Actor.CUSTOMER,
    trigger: "用户在个人中心点击「我的书评」标签或「查看全部」按钮",
    pre-cond: "用户已登录",
    post-cond: "系统显示用户的所有书评",
    priority: Priority.MEDIUM,
    normal-flow: [
      1. 系统获取当前用户的所有书评。
      2. 系统显示书评管理页面，包含书评列表和相关操作。
      3. 用户可以编辑或删除自己的书评。
    ],
  ),
  (
    name: "撰写读书笔记",
    actor: Actor.CUSTOMER,
    trigger: "用户在书籍详情页点击「撰写笔记」按钮或在笔记创建页面",
    pre-cond: "用户已登录",
    post-cond: "读书笔记保存成功",
    priority: Priority.MEDIUM,
    normal-flow: [
      1. 系统检查用户是否已为该书籍创建过笔记。
      2. 系统显示读书笔记编辑页面。
      3. 用户输入笔记标题和内容。
      4. 用户点击「发布笔记」按钮。
      5. 系统验证输入信息的有效性。
      6. 系统将读书笔记信息存入数据库。
      7. 系统跳转到笔记详情页面。
    ],
    alt-flow: [
      - 1a. 如果用户已为该书籍创建过笔记，系统提示用户编辑现有笔记。
    ],
    special-req: "每个用户对每本书只能创建一篇读书笔记。",
  ),
  (
    name: "编辑读书笔记",
    actor: Actor.CUSTOMER,
    trigger: "用户点击笔记的「编辑」按钮",
    pre-cond: "用户已登录，拥有该读书笔记的编辑权限",
    post-cond: "读书笔记更新成功",
    priority: Priority.MEDIUM,
    normal-flow: [
      1. 系统显示读书笔记编辑页面，预填充现有内容。
      2. 用户修改笔记标题或内容。
      3. 用户点击「更新笔记」按钮。
      4. 系统验证输入信息的有效性。
      5. 系统更新数据库中的读书笔记信息。
      6. 系统跳转到笔记详情页面。
    ],
  ),
  (
    name: "删除读书笔记",
    actor: Actor.CUSTOMER,
    trigger: "用户点击笔记的「删除」按钮",
    pre-cond: "用户已登录，拥有该读书笔记的删除权限",
    post-cond: "读书笔记删除成功",
    priority: Priority.MEDIUM,
    normal-flow: [
      1. 系统显示删除确认对话框。
      2. 用户确认删除。
      3. 系统从数据库中删除读书笔记及其相关评论。
      4. 系统提示用户删除成功。
      5. 系统跳转到用户笔记列表页面。
    ],
    alt-flow: [
      - 2a. 如果用户取消删除，系统不执行任何操作。
    ],
  ),
  (
    name: "查看读书笔记详情",
    actor: Actor.CUSTOMER,
    trigger: "用户点击读书笔记标题或「查看详情」按钮",
    pre-cond: "无",
    post-cond: "系统显示读书笔记的详细内容",
    priority: Priority.MEDIUM,
    normal-flow: [
      1. 系统根据笔记 ID 查询笔记详细信息。
      2. 系统同时获取该笔记的所有评论。
      3. 系统显示笔记详情页面，包含笔记内容、互动数据、评论列表等。
      4. 用户可以进行点赞、点踩、评论等操作。
    ],
  ),
  (
    name: "点赞/点踩读书笔记",
    actor: Actor.CUSTOMER,
    trigger: "用户点击笔记的「点赞」或「点踩」按钮",
    pre-cond: "用户已登录",
    post-cond: "用户的反馈记录到系统，笔记的互动数据更新",
    priority: Priority.LOW,
    normal-flow: [
      1. 系统检查用户是否已对该笔记进行过反馈。
      2. 系统记录或更新用户的反馈（点赞/点踩）。
      3. 系统更新笔记的点赞/点踩计数。
      4. 系统更新界面显示。
    ],
    alt-flow: [
      - 1a. 如果用户已进行过相同反馈，系统取消该反馈。
      - 1b. 如果用户已进行过不同反馈，系统更新为新的反馈类型。
    ],
  ),
  (
    name: "评论读书笔记",
    actor: Actor.CUSTOMER,
    trigger: "用户在笔记详情页填写评论并点击「发表评论」按钮",
    pre-cond: "用户已登录",
    post-cond: "评论发表成功，显示在笔记的评论列表中",
    priority: Priority.LOW,
    normal-flow: [
      1. 用户在评论框中输入评论内容。
      2. 用户点击「发表评论」按钮。
      3. 系统验证评论内容的有效性。
      4. 系统将评论信息存入数据库。
      5. 系统更新笔记的评论计数。
      6. 系统更新评论列表显示。
      7. 系统清空评论表单。
    ],
    alt-flow: [
      - 3a. 如果评论内容为空，系统提示用户输入内容。
    ],
  ),
  (
    name: "删除笔记评论",
    actor: Actor.CUSTOMER,
    trigger: "用户点击自己评论的「删除」按钮",
    pre-cond: "用户已登录，拥有该评论的删除权限",
    post-cond: "评论删除成功",
    priority: Priority.LOW,
    normal-flow: [
      1. 系统显示删除确认对话框。
      2. 用户确认删除。
      3. 系统从数据库中删除评论信息。
      4. 系统更新笔记的评论计数。
      5. 系统更新评论列表显示。
    ],
    alt-flow: [
      - 2a. 如果用户取消删除，系统不执行任何操作。
    ],
  ),
  (
    name: "查看用户笔记列表",
    actor: Actor.CUSTOMER,
    trigger: "用户在个人中心点击「我的读书笔记」标签或「查看全部」按钮",
    pre-cond: "用户已登录",
    post-cond: "系统显示用户的所有读书笔记",
    priority: Priority.MEDIUM,
    normal-flow: [
      1. 系统获取当前用户的所有读书笔记。
      2. 系统显示笔记管理页面，包含笔记列表和相关操作。
      3. 用户可以编辑或删除自己的笔记。
    ],
  ),
  (
    name: "创建商品",
    actor: Actor.ADMIN,
    trigger: "管理员在商品管理页面点击「添加商品」按钮",
    pre-cond: "管理员已登录",
    post-cond: "商品创建成功",
    priority: Priority.HIGH,
    normal-flow: [
      1. 系统显示商品创建页面。
      2. 管理员输入商品信息（名称、价格、评分、描述、封面、详情、规格等）。
      3. 管理员可以上传商品封面图片。
      4. 系统验证输入信息的有效性。
      5. 系统将商品信息存入数据库。
      6. 系统提示管理员创建成功。
      7. 系统跳转到商品管理列表页面。
    ],
    alt-flow: [
      - 4a. 如果必填信息缺失，系统提示管理员补充。
    ],
    special-req: "商品封面支持图片上传功能。",
  ),
  (
    name: "更新商品信息",
    actor: Actor.ADMIN,
    trigger: "管理员在商品管理页面点击「编辑」按钮",
    pre-cond: "管理员已登录",
    post-cond: "商品信息更新成功",
    priority: Priority.HIGH,
    normal-flow: [
      1. 系统显示商品编辑页面，预填充现有信息。
      2. 管理员修改商品信息。
      3. 管理员可以重新上传商品封面。
      4. 系统验证输入信息的有效性。
      5. 系统更新数据库中的商品信息。
      6. 系统提示管理员修改成功。
      7. 系统跳转到商品管理列表页面。
    ],
  ),
  (
    name: "删除商品",
    actor: Actor.ADMIN,
    trigger: "管理员在商品管理页面点击「删除」按钮",
    pre-cond: "管理员已登录",
    post-cond: "商品删除成功",
    priority: Priority.HIGH,
    normal-flow: [
      1. 系统显示删除确认对话框。
      2. 管理员确认删除。
      3. 系统从数据库中删除商品及其相关信息。
      4. 系统提示管理员删除成功。
      5. 系统更新商品列表显示。
    ],
    alt-flow: [
      - 2a. 如果管理员取消删除，系统不执行任何操作。
    ],
    special-req: "删除商品时需要考虑关联的订单、书评等数据。",
  ),
  (
    name: "管理商品库存",
    actor: Actor.ADMIN,
    trigger: "管理员在商品管理页面点击「库存」按钮",
    pre-cond: "管理员已登录",
    post-cond: "商品库存更新成功",
    priority: Priority.HIGH,
    normal-flow: [
      1. 系统显示库存管理对话框，显示当前库存信息。
      2. 管理员输入新的可售库存和冻结库存数量。
      3. 系统验证输入数量的有效性（必须为非负整数）。
      4. 系统更新数据库中的商品库存信息。
      5. 系统提示管理员调整成功。
    ],
    alt-flow: [
      - 3a. 如果输入数量无效，系统提示管理员重新输入。
    ],
    special-req: "库存管理需要区分可售库存和冻结库存。",
  ),
  (
    name: "创建广告",
    actor: Actor.ADMIN,
    trigger: "管理员在广告管理页面点击「添加广告」按钮",
    pre-cond: "管理员已登录",
    post-cond: "广告创建成功",
    priority: Priority.MEDIUM,
    normal-flow: [
      1. 系统显示广告创建页面。
      2. 管理员输入广告信息（标题、内容、关联商品 ID）。
      3. 管理员上传广告图片。
      4. 系统验证输入信息的有效性。
      5. 系统将广告信息存入数据库。
      6. 系统提示管理员创建成功。
      7. 系统跳转到广告管理列表页面。
    ],
    alt-flow: [
      - 4a. 如果关联的商品 ID 不存在，系统提示管理员。
    ],
    special-req: "广告必须关联到有效的商品。",
  ),
  (
    name: "更新广告信息",
    actor: Actor.ADMIN,
    trigger: "管理员在广告管理页面点击「编辑」按钮",
    pre-cond: "管理员已登录",
    post-cond: "广告信息更新成功",
    priority: Priority.MEDIUM,
    normal-flow: [
      1. 系统显示广告编辑页面，预填充现有信息。
      2. 管理员修改广告信息。
      3. 管理员可以重新上传广告图片。
      4. 系统验证输入信息的有效性。
      5. 系统更新数据库中的广告信息。
      6. 系统提示管理员修改成功。
      7. 系统跳转到广告管理列表页面。
    ],
  ),
  (
    name: "删除广告",
    actor: Actor.ADMIN,
    trigger: "管理员在广告管理页面点击「删除」按钮",
    pre-cond: "管理员已登录",
    post-cond: "广告删除成功",
    priority: Priority.MEDIUM,
    normal-flow: [
      1. 系统显示删除确认对话框。
      2. 管理员确认删除。
      3. 系统从数据库中删除广告信息。
      4. 系统提示管理员删除成功。
      5. 系统更新广告列表显示。
    ],
    alt-flow: [
      - 2a. 如果管理员取消删除，系统不执行任何操作。
    ],
  ),
  (
    name: "管理书评（管理员）",
    actor: Actor.ADMIN,
    trigger: "管理员进入书评管理页面",
    pre-cond: "管理员已登录",
    post-cond: "书评被编辑或删除",
    priority: Priority.MEDIUM,
    normal-flow: [
      1. 系统显示所有书评列表，支持按用户 ID、商品 ID 筛选。
      2. 管理员可以查看书评详情。
      3. 管理员可以编辑或删除任意书评。
      4. 系统更新数据库中的书评信息。
      5. 系统提示管理员操作成功。
    ],
  ),
  (
    name: "管理读书笔记（管理员）",
    actor: Actor.ADMIN,
    trigger: "管理员进入读书笔记管理页面",
    pre-cond: "管理员已登录",
    post-cond: "读书笔记被编辑或删除",
    priority: Priority.MEDIUM,
    normal-flow: [
      1. 系统显示所有读书笔记列表，支持按用户 ID、商品 ID、标题筛选。
      2. 管理员可以查看笔记详情和互动数据。
      3. 管理员可以编辑或删除任意读书笔记。
      4. 系统更新数据库中的读书笔记信息。
      5. 系统提示管理员操作成功。
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
  ..use-cases
    .enumerate()
    .map(((i, case)) => {
      (
        combine-actor(case.actor),
        link(label("user-case" + str(i + 1)))[用例 #(i + 1)：#case.name],
      )
    })
    .flatten()
)

= 详细用例描述

#for case in use-cases {
  use-case(case)
}
