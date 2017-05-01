/**
 * Created by Chang on 2017/4/25.
 */
$(function () {
    "use strict";

    var table = $('#agentTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": $('body').attr('data-agent-url'),
            "data": function (d) {
                return $.extend({}, d, extendData());
            }
        },
        "ordering": false,
        "lengthChange": false,
        "searching": false,
        "columns": [
            {
                title: '',
                target: 0,
                className: 'treegrid-control table-action',
                data: function (item) {
                    if (item.children) {
                        return '<span><i class="fa fa-chevron-right" aria-hidden="true"></i></span>';
                    }
                    return '';
                }
            },
            {
                "title": "级别", "data": "rank"
            },
            {
                "title": "用户", "data": "name"
            },
            {
                "title": "手机号", "data": "phone"
            },
            {
                "title": "所佣下属", "data": "subordinate"
            },
            {
                title: "操作",
                className: 'table-action',
                data: function (item) {
                    return '<a href="javascript:;" class="js-checkUser" data-id="' + item.id + '"><i class="fa fa-check-circle-o" aria-hidden="true"></i>&nbsp;查看</a>';
                }
            }
        ],
        "treeGrid": {
            "left": 20,
            "expandIcon": '<span><i class="fa fa-chevron-right" aria-hidden="true"></i></span>',
            "collapseIcon": '<span><i class="fa fa-chevron-down" aria-hidden="true"></i></span>'
        },
        "displayLength": 15,
        "drawCallback": function () {
            clearSearchValue();
        }
    });

    $(document).on('click', '.js-search', function () {
        table.ajax.reload();
    }).on('click', '.js-checkUser', function () {
        $('#content', parent.document).attr('src', 'agentDetail.html');
    });

    function extendData() {
        var formItem = $('.js-selectToolbar').find('.form-control');
        if (formItem.length === 0)  return {};
        var data = {};

        formItem.each(function () {
            var t = $(this);
            var n = t.attr('name');
            var v = t.val();
            if (v) data[n] = v;
        });
        return data;
    }

    function clearSearchValue() {
        //TODO
    }
});